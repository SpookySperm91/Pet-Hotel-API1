package john.api1.application.services.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.AdminDomain;
import john.api1.application.dto.mapper.AdminDTO;
import john.api1.application.ports.repositories.admin.IAdminSearchRepository;
import john.api1.application.ports.services.admin.IAdminSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminSearchAS implements IAdminSearch {
    private final IAdminSearchRepository searchRepository;

    @Autowired
    public AdminSearchAS(IAdminSearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public DomainResponse<AdminDTO> searchById(String id) {
        try {
            var account = searchRepository.searchById(id);
            if (account.isEmpty()) throw new PersistenceException("Admin account cannot be found!");

            AdminDomain domain = account.get();
            var dto = new AdminDTO(domain.getId(), domain.getUsername(), domain.getEmail(), domain.isActive());
            return DomainResponse.success(dto, "Admin account successfully retrieved");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception f) {
            return DomainResponse.error("Something wrong with the system, try again later");
        }
    }

    public DomainResponse<AdminDTO> searchByUsername(String username) {
        try {
            var account = searchRepository.searchByUsername(username);
            if (account.isEmpty()) throw new PersistenceException("Admin account cannot be found!");

            AdminDomain domain = account.get();
            var dto = new AdminDTO(domain.getId(), domain.getUsername(), domain.getEmail(), domain.isActive());
            return DomainResponse.success(dto, "Admin account successfully retrieved");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception f) {
            return DomainResponse.error("Something wrong with the system, try again later");
        }
    }

    public DomainResponse<AdminDTO> searchByEmail(String email) {
        try {
            var account = searchRepository.searchByEmail(email);
            if (account.isEmpty()) throw new PersistenceException("Admin account cannot be found!");

            AdminDomain domain = account.get();
            var dto = new AdminDTO(domain.getId(), domain.getUsername(), domain.getEmail(), domain.isActive());
            return DomainResponse.success(dto, "Admin account successfully retrieved");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception f) {
            return DomainResponse.error("Something wrong with the system, try again later");
        }
    }

    public DomainResponse<List<AdminDTO>> searchAllActive() {
        try {
            var list = searchRepository.searchAllActive()
                    .stream()
                    .map(domain -> new AdminDTO(
                            domain.getId(),
                            domain.getUsername(),
                            domain.getEmail(),
                            domain.isActive()))
                    .toList();

            return DomainResponse.success(list, "All active admin accounts successfully retrieved");
        } catch (PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something wrong with the system, try again later");
        }
    }


    // Login
    public Optional<AdminDomain> searchId(String id) {
        var account = searchRepository.searchById(id);
        if (account.isEmpty()) throw new PersistenceException("Admin account cannot be found!");
        return Optional.ofNullable(account.get());
    }

    public Optional<AdminDomain> searchUsername(String username) {
        var account = searchRepository.searchByUsername(username);
        if (account.isEmpty()) throw new PersistenceException("Admin account cannot be found!");
        return Optional.ofNullable(account.get());
    }


}
