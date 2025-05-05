package john.api1.application.ports.repositories.admin;

import john.api1.application.domain.models.AdminDomain;

import java.util.List;
import java.util.Optional;

public interface IAdminSearchRepository {
    Optional<AdminDomain> searchById(String id);

    Optional<AdminDomain> searchByUsername(String username);

    Optional<AdminDomain> searchByEmail(String username);

    List<AdminDomain> searchAllActive();
}
