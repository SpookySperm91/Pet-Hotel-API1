package john.api1.application.components.exception;

public class InvalidEmailLogException extends IllegalArgumentException {
    public InvalidEmailLogException(String message) {
        super(message);
    }
}
