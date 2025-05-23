package john.api1.application.ports.repositories.owner;

import john.api1.application.domain.models.ClientAccountDomain;

public interface IAccountUpdateRepository{
    boolean updateEmail(String id, String newEmail);
    boolean updatePhoneNumber(String id, String newPhoneNumber);
    boolean updatePassword(String id, String newPassword);
    void updateAccount(ClientAccountDomain accountDomain);
}

