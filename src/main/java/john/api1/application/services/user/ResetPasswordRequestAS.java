package john.api1.application.services.user;

import com.mongodb.MongoException;
import john.api1.application.adapters.services.PasswordResetAdapter;
import john.api1.application.async.AsyncEmailService;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.VerificationGenerator;
import john.api1.application.components.enums.EmailType;
import john.api1.application.components.enums.VerificationType;
import john.api1.application.domain.models.VerificationDomain;
import john.api1.application.ports.repositories.IVerificationRepository;
import john.api1.application.ports.repositories.account.IAccountSearchRepository;
import john.api1.application.services.response.RequestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordRequestAS {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordRequestAS.class);
    private final IAccountSearchRepository accountSearch;
    private final IVerificationRepository verificationRepository;
    private final AsyncEmailService emailService;

    public ResetPasswordRequestAS(IAccountSearchRepository accountSearch,
                                  @Qualifier("MongoVerificationRepo") IVerificationRepository verificationRepository,
                                  AsyncEmailService emailService) {
        this.accountSearch = accountSearch;
        this.verificationRepository = verificationRepository;
        this.emailService = emailService;
    }

    // Check if email is valid
    // Process verification
    public DomainResponse<RequestResponse> sendVerificationLink(String email) {
        try {
            return accountSearch.getUsernameIdByEmail(email)
                    .map(account -> processVerification(account.id(), account.username(), email))
                    .orElseGet(() -> {
                        logger.warn("Account not found for email: {}", email);
                        return DomainResponse.success(new RequestResponse(null, null), "If the email exists in our system, a verification link has been sent. Please check your inbox.");
                    });
        } catch (Exception e) {
            return handleException(e, "Error while sending verification link.");
        }
    }


    // Generate verification data
    // Save verification entry in DB
    // Generate reset link
    // Send email
    private DomainResponse<RequestResponse> processVerification(String userId, String username, String email) {
        try {
            // Generate verification data
            VerificationDomain verification = VerificationDomain.createNew(
                    VerificationType.VERIFICATION_LINK,
                    userId,
                    username,
                    VerificationGenerator.generateVerificationLink(),
                    30
            );

            // Save verification entry in DB
            String verificationId = verificationRepository.save(verification);
            logger.info("Verification entry saved for userId: {}", userId);

            // Generate reset link
            String resetLink = PasswordResetAdapter.generateResetLink(userId, verification.getData());
            sendEmail(email, username, resetLink);

            // Success
            return DomainResponse.success(new RequestResponse(userId, verificationId), "Verification link sent.");
        } catch (Exception e) {
            return handleException(e, "Error during verification processing.");
        }
    }

    private void sendEmail(String email, String fullName, String body) {
        emailService.sendEmailAsync(EmailType.RESET_PASSWORD_LINK, fullName, email, body)
                .doOnError(error -> logger.error("Failed to send email to {}: {}", email, error.getMessage()))
                .subscribe();
    }


    private DomainResponse<RequestResponse> handleException(Exception e, String contextMessage) {
        if (e instanceof MongoException) {
            logger.error("{} - Database error: {}", contextMessage, e.getMessage(), e);
            return DomainResponse.error("A database issue occurred. Please try again later.");
        } else if (e instanceof IllegalArgumentException) {
            logger.warn("{} - Invalid input: {}", contextMessage, e.getMessage());
            return DomainResponse.error("Invalid input. Please check your request.");
        } else if (e instanceof RuntimeException) {
            logger.error("{} - Unexpected application error: {}", contextMessage, e.getMessage(), e);
            return DomainResponse.error("An unexpected issue occurred. Please contact support.");
        } else {
            logger.error("{} - Unknown error: {}", contextMessage, e.getMessage(), e);
            return DomainResponse.error("An unknown error occurred.");
        }
    }


}
