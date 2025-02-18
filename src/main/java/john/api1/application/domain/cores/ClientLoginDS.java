package john.api1.application.domain.cores;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.PasswordManagement;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.ports.persistence.IAccountSearchRepository;

import java.util.Optional;
import java.util.function.Function;

public class ClientLoginDS {
    private final IAccountSearchRepository accountSearch;
    private final PasswordManagement passwordManagement;

    public ClientLoginDS(IAccountSearchRepository accountSearch, PasswordManagement passwordManagement) {
        this.accountSearch = accountSearch;
        this.passwordManagement = passwordManagement;
    }

    public DomainResponse<ClientAccountDomain> loginByEmail(String email, String password) {
        return login(email, password, VerificationType.EMAIL);
    }

    public DomainResponse<ClientAccountDomain> loginByPhoneNumber(String phoneNumber, String password) {
        return login(phoneNumber, password, VerificationType.PHONE_NUMBER);
    }

    private DomainResponse<ClientAccountDomain> login(String accountValue, String password, VerificationType type) {
        Function<String, Optional<ClientAccountDomain>> searchFunction = switch (type) {
            case EMAIL -> accountSearch::getAccountByEmail;
            case PHONE_NUMBER -> accountSearch::getAccountByPhoneNumber;
        };

        return Optional.ofNullable(accountValue)
                .filter(value -> !value.isBlank())
                .flatMap(searchFunction)
                .filter(account -> account.validatePassword(password, passwordManagement))
                .map(account -> DomainResponse.success(account, "SUCCESS: Login successful."))
                .orElseGet(() -> DomainResponse.error("ERROR: Invalid credentials."));
    }

    private enum VerificationType {
        EMAIL, PHONE_NUMBER;
    }
}
