package john.api1.application.domain.ports.repositories;

public interface ILoggingRepository {
    void logFailedEmail(String recipientEmail, String recipientUsername, String emailType, String body, String errorMessage);
}
