package john.api1.application.domain.services;

import john.api1.application.domain.ports.contracts.AccountService;
import john.api1.application.domain.ports.persistence.IAccountSearchRepository;
import john.api1.application.domain.ports.persistence.ICreateRepository;

import java.util.List;
import java.util.Optional;

public abstract class AccountServiceImpl<T, ID>
        implements AccountService<T, ID> {
    protected final IAccountSearchRepository<T, ID> searchRepository;
    protected final ICreateRepository<T> createRepository;

    public AccountServiceImpl(
            IAccountSearchRepository<T, ID> searchRepository,
            ICreateRepository<T> createRepository) {
        this.searchRepository = searchRepository;
        this.createRepository = createRepository;
    }

    public T createAccount(T newAccount) {
        return createRepository.createEntity(newAccount);
    }

    public Optional<T> getAccountById(ID id) {
        return searchRepository.getAccountById(id);
    }

    public Optional<T> getAccountByEmail(String email) {
        return searchRepository.getAccountByEmail(email);
    }

    public Optional<T> getAccountByPhoneNumber(String phoneNumber) {
        return searchRepository.getAccountByPhoneNumber(phoneNumber);
    }

    public List<T> getAllAccount() {
        return searchRepository.getAllAccount();
    }
}
