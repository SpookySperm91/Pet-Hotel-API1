package john.api1.application.adapters.repositories.client;

import john.api1.application.ports.repositories.IAccountUpdateRepository;
import org.springframework.stereotype.Repository;

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
