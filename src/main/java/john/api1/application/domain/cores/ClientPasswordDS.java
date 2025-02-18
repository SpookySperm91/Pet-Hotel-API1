package john.api1.application.domain.cores;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.PasswordManagement;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.ports.persistence.IAccountSearchRepository;


public class ClientPasswordDS {
    private final IAccountSearchRepository accountSearch;
    private final PasswordManagement passwordManagement;


    public ClientPasswordDS(IAccountSearchRepository accountSearch, PasswordManagement passwordManagement) {
        this.accountSearch = accountSearch;
        this.passwordManagement = passwordManagement;
    }

    public DomainResponse<ClientAccountDomain> changePassword(String id, String newPassword) {
        if (id == null || id.isEmpty()) return DomainResponse.error("ERROR: Invalid account id");

        try {
            var account = accountSearch.getAccountById(id);
            if (account.isEmpty()) {
                return DomainResponse.error("ERROR: Account not found");
            }

            ClientAccountDomain updatedAccount = account.get().changePassword(newPassword, passwordManagement);
            if (updatedAccount == account.get()) {
                return DomainResponse.error("ERROR: Invalid password format or weak password");
            }

            return DomainResponse.success(updatedAccount, "SUCCESS: Password Reset Successfully");
        } catch (Exception e) {
            return DomainResponse.error("ERROR: An unexpected error occurred while changing the password");
        }
    }

}
