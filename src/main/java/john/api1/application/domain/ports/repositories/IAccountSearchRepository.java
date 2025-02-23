package john.api1.application.domain.ports.repositories;

import john.api1.application.domain.models.ClientAccountDomain;

import java.util.List;
import java.util.Optional;

public interface IAccountSearchRepository {
    Optional<ClientAccountDomain> getAccountById(String id);

    Optional<ClientAccountDomain> getAccountByEmail(String email);

    Optional<ClientAccountDomain> getAccountByPhoneNumber(String phoneNumber);

    List<ClientAccountDomain> getAllAccount();
}
