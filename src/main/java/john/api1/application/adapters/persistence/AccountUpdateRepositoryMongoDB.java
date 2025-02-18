package john.api1.application.adapters.persistence;

import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.ports.persistence.IAccountUpdateRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountUpdateRepositoryMongoDB implements IAccountUpdateRepository {

    @Override
    public boolean updateEmail(String id, String newEmail) {
        return true;
    }

    @Override
    public boolean updatePhoneNumber(String id, String newPhoneNumber) {
        return true;
    }

    @Override
    public boolean updatePassword(String id, String newPassword) {
        return true;
    }
}
