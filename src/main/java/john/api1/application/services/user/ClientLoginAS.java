package john.api1.application.services.user;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.AccountCredentialType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.ClientLoginDS;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.ports.repositories.account.IAccountSearchRepository;
import john.api1.application.ports.services.ILoginPetOwner;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientLoginAS implements ILoginPetOwner {
    private static final Logger logger = LoggerFactory.getLogger(ClientLoginAS.class);
    private final IAccountSearchRepository searchRepository;
    private final ClientLoginDS clientLogin;

    @Autowired
    public ClientLoginAS(@Qualifier("MongoAccountSearchRepo") IAccountSearchRepository searchRepository, ClientLoginDS clientLogin) {
        this.searchRepository = searchRepository;
        this.clientLogin = clientLogin;
    }

    private Optional<ClientAccountDomain> checkEmail(String email) {
        return searchRepository.getAccountByEmail(email);
    }

    private Optional<ClientAccountDomain> checkPhoneNumber(String phoneNumber) {
        return searchRepository.getAccountByPhoneNumber(phoneNumber);
    }

    // Check if email or phone number
    // Login with password
    // return response (if success, return with userId)
    @Override
    public DomainResponse<String> login(AccountCredentialType type, String userAccount, String password) {
        try {
            var account = switch (type) {
                case EMAIL -> checkEmail(userAccount);
                case PHONE_NUMBER -> checkPhoneNumber(userAccount);
            };

            if (account.isEmpty()) {
                return DomainResponse.error("Invalid account");
            }

            var loginOperation = clientLogin.login(account.get(), password);
            if (!loginOperation.isSuccess()) {
                return DomainResponse.error(loginOperation.getMessage());
            }
            // return success
            return DomainResponse.success(loginOperation.getData(), loginOperation.getMessage());

        } catch (PersistenceException | DomainArgumentException e) {
            logger.error("Error from login: {}", e.getMessage(), e);
            return DomainResponse.error(exceptionMessage(e));
        }
    }


    @Override
    public DomainResponse<Boolean> logout(String userId) {
        if (!ObjectId.isValid(userId)) {
            return DomainResponse.error(false, "Invalid user ID");
        }

        var account = searchRepository.getAccountById(new ObjectId(userId));
        return account.isPresent()
                ? DomainResponse.success(true, "Logout successful")
                : DomainResponse.error(false, "Account does not exist");
    }

    private String exceptionMessage(RuntimeException e) {
        return (e instanceof DomainArgumentException) ? e.getMessage()
                : (e instanceof PersistenceException) ? "A database error occurred. Please try again."
                : "An unexpected error occurred. Please contact support.";
    }

}
