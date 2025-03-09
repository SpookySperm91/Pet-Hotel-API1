package john.api1.application.domain.cores;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.PasswordManagement;
import john.api1.application.domain.models.ClientAccountDomain;


public class ClientChangePasswordDS {
    private final PasswordManagement passwordManagement;


    public ClientChangePasswordDS(PasswordManagement passwordManagement) {
        this.passwordManagement = passwordManagement;
    }

    public DomainResponse<ClientAccountDomain> changePassword(String newPassword, ClientAccountDomain account) {
        if (!passwordManagement.isValid(newPassword)) {
            return DomainResponse.error("Password must be at least 8 characters long and include a special character.");
        }

        ClientAccountDomain updatedPassword = account.changePassword(newPassword, passwordManagement);

        if (!passwordManagement.validation(newPassword, updatedPassword.getHashedPassword())) {
            return DomainResponse.error("Password update failed, please try again.");
        }

        return DomainResponse.success(updatedPassword, "Password Reset Successfully");
    }

}
