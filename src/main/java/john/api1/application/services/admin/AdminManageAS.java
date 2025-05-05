package john.api1.application.services.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.PasswordManagement;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.AdminDomain;
import john.api1.application.ports.repositories.admin.IAdminManageRepository;
import john.api1.application.ports.repositories.admin.IAdminSearchRepository;
import john.api1.application.ports.services.admin.IAdminManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminManageAS implements IAdminManage {
    private final IAdminManageRepository manageRepository;
    private final IAdminSearchRepository searchRepository;
    private final PasswordManagement passwordManagement;


    @Autowired
    public AdminManageAS(IAdminManageRepository manageRepository,
                         IAdminSearchRepository searchRepository,
                         PasswordManagement passwordManagement) {
        this.manageRepository = manageRepository;
        this.searchRepository = searchRepository;
        this.passwordManagement = passwordManagement;
    }

    @Override
    public DomainResponse<Void> updateUsername(String id, String username) {
        try {
            var account = searchRepository.searchById(id);
            if (account.isEmpty()) throw new PersistenceException("Admin cannot be found");

            AdminDomain domain = account.get();
            domain = domain.changeUsername(username);

            manageRepository.updateAdmin(domain);
            return DomainResponse.success("Successfully update admin username");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something wrong with the system, try again");
        }
    }

    @Override
    public DomainResponse<Void> updateEmail(String id, String email) {
        try {
            var account = searchRepository.searchById(id);
            if (account.isEmpty()) throw new PersistenceException("Admin cannot be found");

            AdminDomain domain = account.get();
            domain = domain.changeEmail(email);

            manageRepository.updateAdmin(domain);
            return DomainResponse.success("Successfully update admin email");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something wrong with the system, try again");
        }
    }

    @Override
    public DomainResponse<Void> updatePassword(String id, String password) {
        try {
            var account = searchRepository.searchById(id);
            if (account.isEmpty()) throw new PersistenceException("Admin cannot be found");

            AdminDomain domain = account.get();
            domain = domain.changePassword(password, passwordManagement);

            manageRepository.updateAdmin(domain);
            return DomainResponse.success("Successfully update admin password");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something wrong with the system, try again");
        }
    }

    @Override
    public DomainResponse<Void> deleteAdminById(String id) {
        try {
            manageRepository.deleteAdminById(id);
            return DomainResponse.success("Successfully deleted admin #" + id);

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something wrong with the system, try again");
        }
    }

    @Override
    public DomainResponse<Void> deleteInactiveAdmin() {
        try {
            var count = manageRepository.deleteInactiveAdmin();
            return DomainResponse.success("Successfully deleted all inactive admin. Inactive deleted:" + count);

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something wrong with the system, try again");
        }
    }
}
