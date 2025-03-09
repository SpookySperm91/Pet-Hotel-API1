package john.api1.application.ports.repositories.account;

public interface IAccountUpdateRepository{
    boolean updateEmail(String id, String newEmail);
    boolean updatePhoneNumber(String id, String newPhoneNumber);
    boolean updatePassword(String id, String newPassword);
}

