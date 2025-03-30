package john.api1.application.ports.repositories.owner;

import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.ports.repositories.wrapper.ClientFullAccount;
import john.api1.application.ports.repositories.wrapper.UsernameAndId;

import java.util.List;
import java.util.Optional;

public interface IAccountSearchRepository {
    Optional<ClientAccountDomain> getAccountById(String id);

    Optional<ClientAccountDomain> getAccountByEmail(String email);

    Optional<ClientAccountDomain> getAccountByPhoneNumber(String phoneNumber);

    List<ClientAccountDomain> getAllAccount();

    // ReadOnly
    Optional<UsernameAndId> getUsernameIdByEmail(String email);

    // Full Domain Object
    // 🔹 Full Profile (Used for login & profile retrieval)
    Optional<ClientFullAccount> getFullAccountById(String id);

    // If exist
    boolean existById(String id);
}
