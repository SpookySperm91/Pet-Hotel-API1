package john.api1.application.ports.contracts;

public interface UpdateAccountService<T> {
    T updateEmail(T account, String newEmail);

    T updatePhoneNumber(T account, String newPhoneNumber);
}
