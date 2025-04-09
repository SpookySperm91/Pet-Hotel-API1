package john.api1.application.ports.repositories.request;

public interface IRequestScheduledUpdateRepository {
    int markRejectedAsInactiveAfterFiveMinutes();
}
