package john.api1.application.services.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.PasswordManagement;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.AdminDomain;
import john.api1.application.dto.mapper.AdminDTO;
import john.api1.application.dto.request.admin.AdminCreateRDTO;
import john.api1.application.ports.repositories.admin.IAdminCreateRepository;
import john.api1.application.ports.repositories.admin.IAdminSearchRepository;
import john.api1.application.ports.services.admin.IAdminCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminCreateNewAS implements IAdminCreate {
    private final IAdminCreateRepository createRepository;
    private final IAdminSearchRepository searchRepository;
    private final PasswordManagement passwordManagement;


    @Autowired
    public AdminCreateNewAS(IAdminCreateRepository createRepository,
                            IAdminSearchRepository searchRepository,
                            PasswordManagement passwordManagement) {
        this.createRepository = createRepository;
        this.searchRepository = searchRepository;
        this.passwordManagement = passwordManagement;
    }

    public DomainResponse<AdminDTO> registerNewAdmin(AdminCreateRDTO request) {
        try {
            if (!AdminDomain.isValidEmail(request.getEmail()))
                throw new DomainArgumentException("Invalid email format");

            if (searchRepository.searchByUsername(request.getUsername()).isPresent())
                throw new PersistenceException("Username is already in use!");

            if (searchRepository.searchByEmail(request.getEmail()).isPresent())
                throw new PersistenceException("Email is already in use!");

            var account = AdminDomain.create(request.getUsername(), request.getEmail());
            account.setPassword(request.getPassword(), passwordManagement);

            var save = createRepository.create(account);
            if (save.isEmpty()) {
                throw new PersistenceException("Admin registration failed");
            }
            var dto = new AdminDTO(save.get(), account.getUsername(), account.getEmail(), account.isActive());
            return DomainResponse.success(dto, "New admin account successfully created");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

}
