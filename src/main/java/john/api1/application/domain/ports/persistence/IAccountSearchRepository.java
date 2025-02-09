package john.api1.application.domain.ports.persistence;

import java.util.List;
import java.util.Optional;

public interface IAccountSearchRepository<T, ID> {
    Optional<T> getAccountById(ID id);

    Optional<T> getAccountByEmail(String email);

    Optional<T> getAccountByPhoneNumber(String phoneNumber);

    List<T> getAllAccount();
}
