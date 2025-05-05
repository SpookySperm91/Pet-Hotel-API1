package john.api1.application.ports.services.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.AdminDTO;
import john.api1.application.dto.request.admin.AdminLoginRDTO;

public interface IAdminLogin {
    DomainResponse<AdminDTO> login(AdminLoginRDTO request);

    DomainResponse<Void> logout(String id);

    DomainResponse<Void> requestPasswordReset(String email);

    DomainResponse<Void> verifyPasswordLink(String id, String token);

    DomainResponse<Void> changePassword(String token, String id, String password);
}
