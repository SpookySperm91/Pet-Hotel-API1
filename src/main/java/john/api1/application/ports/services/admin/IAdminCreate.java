package john.api1.application.ports.services.admin;


import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.AdminDTO;
import john.api1.application.dto.request.admin.AdminCreateRDTO;

public interface IAdminCreate {
    DomainResponse<AdminDTO> registerNewAdmin(AdminCreateRDTO request);
}
