package john.api1.application.domain.ports.contracts;

import java.util.List;
import java.util.Optional;

public interface AccountService<T, ID> {
    T createAccount(T newAccount);

    Optional<T> getAccountById(ID id);

    Optional<T> getAccountByEmail(String email);

    Optional<T> getAccountByPhoneNumber(String phoneNumber);

    List<T> getAllAccount();
}
