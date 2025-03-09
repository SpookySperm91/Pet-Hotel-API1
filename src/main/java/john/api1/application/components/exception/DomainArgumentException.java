package john.api1.application.components.exception;

public class DomainArgumentException extends IllegalArgumentException {
    public DomainArgumentException(String message) {
        super(message);
    }
}
