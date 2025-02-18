package john.api1.application.services;

import john.api1.application.adapters.persistence.entities.ClientEntity;
import john.api1.application.components.PasswordManagement;
import john.api1.application.domain.ports.contracts.ClientServiceDomain;
import john.api1.application.domain.ports.persistence.IAccountSearchRepository;
import john.api1.application.domain.ports.persistence.ICreateRepository;

//
// BASIC CRUD FUNCTION FOR CLIENT-ACCOUNT MANAGEMENT
//
public abstract class ClientServiceDomainImpl
        implements ClientServiceDomain {
    protected final IAccountSearchRepository searchRepository;
    protected final ICreateRepository<ClientEntity> createRepository;
    protected final PasswordManagement passwordValidation;

    public ClientServiceDomainImpl(
            IAccountSearchRepository searchRepository,
            ICreateRepository<ClientEntity> createRepository,
            PasswordManagement passwordValidation) {
        this.searchRepository = searchRepository;
        this.createRepository = createRepository;
        this.passwordValidation = passwordValidation;
    }

    public ClientEntity createClientAccount(String email, String password) {
        if (searchRepository.getAccountByEmail(email).isPresent()) {
            return null;
        }

        return createRepository.createEntity(new ClientEntity());
    }

    public boolean verifyPassword(ClientEntity account, String password) {
        return false;
    }
}
