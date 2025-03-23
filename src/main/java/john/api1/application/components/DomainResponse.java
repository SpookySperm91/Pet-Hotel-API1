package john.api1.application.components;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DomainResponse<T> {
    public enum ErrorType {NONE, VALIDATION_ERROR, NOT_FOUND, SERVER_ERROR}

    private T data;
    private String message;
    private boolean success;
    private ErrorType errorType;


    public static <T> DomainResponse<T> success() {
        return new DomainResponse<>(null, null, true, null);
    }

    public static <T> DomainResponse<T> success(T data) {
        return new DomainResponse<>(data, null, true, null);
    }

    public static <T> DomainResponse<T> success(T data, String message) {
        return new DomainResponse<>(data, message, true, null);
    }

    public static <T> DomainResponse<T> success(String message) {
        return new DomainResponse<>(null, message, true, null);
    }

    public static <T> DomainResponse<T> error(T data) {
        return new DomainResponse<>(data, null, false, null);
    }

    public static <T> DomainResponse<T> error(T data, String message) {
        return new DomainResponse<>(data, message, false, null);
    }

    public static <T> DomainResponse<T> error(String errorMessage) {
        return new DomainResponse<>(null, errorMessage, false, null);
    }

    public static <T> DomainResponse<T> error(T data, String message, ErrorType errorType) {
        return new DomainResponse<>(data, message, false, errorType);
    }

    public static <T> DomainResponse<T> error(String errorMessage, ErrorType errorType) {
        return new DomainResponse<>(null, errorMessage, false, errorType);
    }


}
