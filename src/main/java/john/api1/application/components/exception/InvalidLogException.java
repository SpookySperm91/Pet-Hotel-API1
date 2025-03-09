package john.api1.application.components.exception;

public class InvalidLogException extends IllegalArgumentException {
    public InvalidLogException(String message) {
        super(message);
    }
}
