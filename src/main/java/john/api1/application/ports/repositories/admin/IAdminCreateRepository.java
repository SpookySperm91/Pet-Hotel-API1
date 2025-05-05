package john.api1.application.ports.repositories.admin;

import john.api1.application.domain.models.AdminDomain;

import java.util.Optional;

public interface IAdminCreateRepository {
    Optional<String> create(AdminDomain domain);
}
