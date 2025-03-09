package john.api1.application.ports.repositories.account;

import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.ports.repositories.records.UsernameAndId;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface IAccountSearchRepository {
    Optional<ClientAccountDomain> getAccountById(ObjectId id);

    Optional<ClientAccountDomain> getAccountByEmail(String email);

    Optional<ClientAccountDomain> getAccountByPhoneNumber(String phoneNumber);

    List<ClientAccountDomain> getAllAccount();

    // ReadOnly
    Optional<UsernameAndId> getUsernameIdByEmail(String email);

}
