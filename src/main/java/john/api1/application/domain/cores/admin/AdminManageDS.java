package john.api1.application.domain.cores.admin;

import john.api1.application.components.PasswordManagement;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.domain.models.AdminDomain;

public class AdminManageDS {

    // Check username
    // Check password
    // Return
    public static void login(String username, String password, AdminDomain domain, PasswordManagement management) {
        if(!domain.getUsername().equals(username)) {
            throw new DomainArgumentException("Invalid username");
        }

        if (!domain.validatePassword(password, management)) {
            throw new DomainArgumentException("Invalid password");
        }
    }
}
