package john.api1.application.domain.ports.contracts;

import john.api1.application.adapters.repositories.entities.ClientEntity;

public interface ClientServiceDomain {
    ClientEntity createClientAccount(ClientEntity newAccount);
    ClientEntity updateAccountPassword(ClientEntity account, String newPassword);
    boolean verifyPassword(ClientEntity account, String password);
}
