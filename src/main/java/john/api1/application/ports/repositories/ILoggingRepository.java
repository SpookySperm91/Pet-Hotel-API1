package john.api1.application.ports.repositories;

public interface ILoggingRepository {
    void logFailedEmail(String recipientEmail, String recipientUsername, String emailType, String body, String errorMessage);
}
