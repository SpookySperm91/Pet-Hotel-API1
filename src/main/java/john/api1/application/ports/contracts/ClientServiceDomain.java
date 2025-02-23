package john.api1.application.ports.contracts;

import john.api1.application.adapters.repositories.ClientEntity;

public interface ClientServiceDomain {
    ClientEntity createClientAccount(ClientEntity newAccount);
    ClientEntity updateAccountPassword(ClientEntity account, String newPassword);
    boolean verifyPassword(ClientEntity account, String password);
}
