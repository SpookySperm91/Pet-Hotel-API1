package john.api1.application.components;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DomainResponse<T> {
    private T data;
    private String message;
    private boolean success;

    public static <T> DomainResponse<T> success(T data) {
        return new DomainResponse<>(data, null, true);
    }

    public static <T> DomainResponse<T> success(T data, String message) {
        return new DomainResponse<>(data, message, true);
    }

    public static <T> DomainResponse<T> success(String message) {
        return new DomainResponse<>(null, message, true);
    }

    public static <T> DomainResponse<T> error(T data) {
        return new DomainResponse<>(data, null, false);
    }

    public static <T> DomainResponse<T> error(T data, String message) {
        return new DomainResponse<>(data, message, false);
    }

    public static <T> DomainResponse<T> error(String errorMessage) {
        return new DomainResponse<>(null, errorMessage, false);
    }
}
