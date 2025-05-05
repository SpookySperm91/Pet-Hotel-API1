package john.api1.application.services.admin;

import john.api1.application.adapters.services.PasswordResetAdapter;
import john.api1.application.async.AsyncEmailService;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.PasswordManagement;
import john.api1.application.components.VerificationGenerator;
import john.api1.application.components.enums.EmailType;
import john.api1.application.components.enums.VerificationType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.AdminDomain;
import john.api1.application.domain.models.VerificationDomain;
import john.api1.application.dto.mapper.AdminDTO;
import john.api1.application.dto.request.admin.AdminLoginRDTO;
import john.api1.application.ports.repositories.IVerificationRepository;
import john.api1.application.ports.services.admin.IAdminLogin;
import john.api1.application.ports.services.admin.IAdminManage;
import john.api1.application.ports.services.admin.IAdminSearch;
import john.api1.application.services.user.ResetPasswordRequestAS;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginAS implements IAdminLogin {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordRequestAS.class);
    private final IAdminSearch adminSearch;
    private final IAdminManage adminManage;
    private final PasswordManagement passwordManagement;
    private final AsyncEmailService emailService;
    private final IVerificationRepository verification;

    @Autowired
    public AdminLoginAS(IAdminSearch adminSearch,
                        IAdminManage adminManage,
                        PasswordManagement passwordManagement,
                        AsyncEmailService emailService,
                        IVerificationRepository verificationSave) {
        this.adminSearch = adminSearch;
        this.adminManage = adminManage;
        this.passwordManagement = passwordManagement;
        this.emailService = emailService;
        this.verification = verificationSave;
    }

    @Override
    public DomainResponse<AdminDTO> login(AdminLoginRDTO request) {
        try {
            var login = adminSearch.searchUsername(request.getUsername());
            if (login.isEmpty()) throw new PersistenceException("Invalid username or password password");

            AdminDomain admin = login.get();
            if (admin.validatePassword(request.getPassword(), passwordManagement)) {
                AdminDTO dto = AdminDTO.map(admin);
                return DomainResponse.success(dto, "Admin login successfully");
            }
            return DomainResponse.error("Invalid username or password password");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    @Override
    public DomainResponse<Void> logout(String id) {
        try {
            var login = adminSearch.searchId(id);
            if (login.isEmpty()) throw new PersistenceException("Admin account cannot be found");
            return DomainResponse.success("Admin account successfully logout");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    @Override
    public DomainResponse<Void> requestPasswordReset(String email) {
        try {
            var account = adminSearch.searchByEmail(email);
            if (!account.isSuccess()) return DomainResponse.error(account.getMessage());

            AdminDTO admin = account.getData();
            VerificationDomain verification = VerificationDomain.createNew(
                    VerificationType.VERIFICATION_LINK,
                    admin.id(),
                    admin.username(),
                    VerificationGenerator.generateVerificationLink(),
                    30
            );

            String resetLink = PasswordResetAdapter.generateResetLinkAdmin(admin.id(), verification.getData());
            sendEmail(admin.email(), admin.username(), resetLink);

            return DomainResponse.success("Check email to confirm its a valid email for verification link.");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception f) {
            return DomainResponse.error("Something wrong with the system. try again later");
        }
    }

    private void sendEmail(String email, String fullName, String body) {
        emailService.sendEmailAsync(EmailType.ADMIN_RESET_PASSWORD_LINK, fullName, email, body)
                .doOnError(error -> logger.error("Failed to send email to {}: {}", email, error.getMessage()))
                .subscribe();
    }

    @Override
    public DomainResponse<Void> verifyPasswordLink(String id, String token) {
        if (!ObjectId.isValid(id)) return DomainResponse.error("Invalid id cannot be format to ObjectId");

        // Check if valid or not
        var link = verification.getByIdAndValue(new ObjectId(id), token);

        if (link.isEmpty()) return DomainResponse.error("Invalid verification link.");
        if (!link.get().isValid()) return DomainResponse.error("This verification link has expired.");

        return DomainResponse.success("Verification link is valid. Proceed to change password");
    }


    @Override
    public DomainResponse<Void> changePassword(String token, String id, String password) {
        try {
            var check = verifyPasswordLink(id, token);
            if (!check.isSuccess())
                return DomainResponse.error(check.getMessage());

            var reset = adminManage.updatePassword(id, password);
            if (!reset.isSuccess()) return DomainResponse.error(reset.getMessage());

            if (!verification.updateStatusToUsed(id, token)) {
                return DomainResponse.error("Password changed, but verification token update failed.");
            }

            return DomainResponse.success(reset.getMessage());
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }
}
