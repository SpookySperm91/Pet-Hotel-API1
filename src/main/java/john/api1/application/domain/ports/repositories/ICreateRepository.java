package john.api1.application.domain.ports.repositories;

import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.models.ClientDomain;

public interface ICreateRepository {
    String createNewClient(ClientAccountDomain newAccount, ClientDomain newClient);
}
