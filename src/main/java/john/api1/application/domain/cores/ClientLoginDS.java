package john.api1.application.domain.cores;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.PasswordManagement;
import john.api1.application.domain.models.ClientAccountDomain;

import java.util.Optional;

public class ClientLoginDS {
    private final PasswordManagement passwordManagement;

    public ClientLoginDS(PasswordManagement passwordManagement) {
        this.passwordManagement = passwordManagement;
    }

    public DomainResponse<String> login(ClientAccountDomain account, String password) {
        return Optional.ofNullable(account.getId())
                .filter(a -> account.validatePassword(password, passwordManagement))
                .map(a -> DomainResponse.success(account.getId(), "Login successful."))
                .orElseGet(() -> DomainResponse.error("Invalid credentials."));
    }
}
