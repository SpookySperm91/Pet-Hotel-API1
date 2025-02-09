package john.api1.application.domain.ports.contracts;

public interface UpdateAccountService<T> {
    T updateEmail(T account, String newEmail);

    T updatePhoneNumber(T account, String newPhoneNumber);
}
