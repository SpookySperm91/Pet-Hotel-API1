package john.api1.application.domain.ports.persistence;
public interface IAccountUpdateRepository<T, ID> extends IUpdateRepository<T, ID> {
    T updateEmail(T account, String newEmail);

    T updatePhoneNumber(T account, String newPhoneNumber);
}

