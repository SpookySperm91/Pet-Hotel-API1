package john.api1.application.ports.repositories.admin;

import john.api1.application.domain.models.AdminDomain;

public interface IAdminManageRepository {
    void updateAdmin(AdminDomain domain);
    void deleteAdminById(String id);
    long deleteInactiveAdmin();

}