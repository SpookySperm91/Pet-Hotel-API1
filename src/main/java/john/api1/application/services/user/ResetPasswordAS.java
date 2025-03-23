package john.api1.application.services.user;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.domain.cores.ClientChangePasswordDS;
import john.api1.application.ports.repositories.IVerificationRepository;
import john.api1.application.ports.repositories.account.IAccountSearchRepository;
import john.api1.application.ports.repositories.account.IAccountUpdateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordAS {
    private final IAccountSearchRepository searchRepository;
    private final IAccountUpdateRepository updateRepository;
    private final IVerificationRepository verificationRepository;
    private final ClientChangePasswordDS changePassword;

    public ResetPasswordAS(@Qualifier("MongoAccountSearchRepo") IAccountSearchRepository searchRepository,
                           @Qualifier("MongoAccountUpdateRepo") IAccountUpdateRepository updateRepository,
                           @Qualifier("MongoVerificationRepo") IVerificationRepository verificationRepository,
                           ClientChangePasswordDS changePassword) {
        this.searchRepository = searchRepository;
        this.updateRepository = updateRepository;
        this.verificationRepository = verificationRepository;
        this.changePassword = changePassword;
    }


    public DomainResponse<String> resetPassword(String id, String token, String newPassword) {
        try {
            // 🔹 Step 1: Fetch the user account
            if(!ObjectId.isValid(id)) return DomainResponse.error("Id cannot be read");

            var userAccount = searchRepository.getAccountById(id);
            if (userAccount.isEmpty()) {
                return DomainResponse.error("User not found!");
            }

            // 🔹 Step 2: Validate & Hash New Password
            var mechanism = changePassword.changePassword(newPassword, userAccount.get());
            if (!mechanism.isSuccess()) {
                return DomainResponse.error(mechanism.getMessage());
            }

            // 🔹 Step 3: Update the password in the database
            boolean passwordUpdated = updateRepository.updatePassword(
                    mechanism.getData().getId(),
                    mechanism.getData().getHashedPassword()
            );

            if (!passwordUpdated) {
                return DomainResponse.error("Failed to update password. Try again.");
            }

            // 🔹 Step 4: Invalidate the verification token
            boolean verificationUpdated = verificationRepository.updateStatusToUsed(id, token);
            if (!verificationUpdated) {
                return DomainResponse.error("Password changed, but verification token update failed.");
            }

            return DomainResponse.success(null, "Password reset successfully!");
        } catch (MongoException e) {
            return DomainResponse.error("Database error. Please try again.");
        }
    }

}

