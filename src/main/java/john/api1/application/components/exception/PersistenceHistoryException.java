package john.api1.application.components.exception;

public class PersistenceHistoryException extends RuntimeException {
    public PersistenceHistoryException(String message, Throwable cause) {
        super(message, cause);
    }
    public PersistenceHistoryException(String message) {
        super(message);
    }

}
