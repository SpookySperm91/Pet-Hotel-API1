package john.api1.application.domain.ports.persistence;

import john.api1.application.domain.models.ClientAccountDomain;

import java.util.Optional;

public interface IAccountUpdateRepository {
    boolean updateEmail(String id, String newEmail);
    boolean updatePhoneNumber(String id, String newPhoneNumber);
    boolean updatePassword(String id, String newPassword);
}

