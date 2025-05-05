package john.api1.application.ports.services.admin;

import john.api1.application.components.DomainResponse;

public interface IAdminManage {
    DomainResponse<Void> updateUsername(String id, String username);

    DomainResponse<Void> updateEmail(String id, String email);

    DomainResponse<Void> updatePassword(String id, String password);

    DomainResponse<Void> deleteAdminById(String id);

    DomainResponse<Void> deleteInactiveAdmin();
}
