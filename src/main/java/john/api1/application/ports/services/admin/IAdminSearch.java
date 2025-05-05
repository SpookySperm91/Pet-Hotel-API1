package john.api1.application.ports.services.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.domain.models.AdminDomain;
import john.api1.application.dto.mapper.AdminDTO;

import java.util.List;
import java.util.Optional;

public interface IAdminSearch {
    DomainResponse<AdminDTO> searchById(String id);

    DomainResponse<AdminDTO> searchByUsername(String username);

    DomainResponse<AdminDTO> searchByEmail(String username);

    DomainResponse<List<AdminDTO>> searchAllActive();

    // login
    Optional<AdminDomain> searchId(String id);

    Optional<AdminDomain> searchUsername(String username);

}
