package john.api1.application.domain.ports.persistence;

import john.api1.application.adapters.persistence.entities.ClientEntity;
import john.api1.application.domain.models.ClientAccountDomain;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface IAccountSearchRepository {
    Optional<ClientAccountDomain> getAccountById(String id);

    Optional<ClientAccountDomain> getAccountByEmail(String email);

    Optional<ClientAccountDomain> getAccountByPhoneNumber(String phoneNumber);

    List<ClientAccountDomain> getAllAccount();
}
