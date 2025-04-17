package john.api1.application.components.exception;

public class XSSDetectedException extends RuntimeException {
    public XSSDetectedException(String message) {
        super(message);
    }
}
