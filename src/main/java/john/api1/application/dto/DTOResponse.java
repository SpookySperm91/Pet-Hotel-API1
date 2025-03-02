package john.api1.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class DTOResponse<T> {
    private int status;
    private Instant requestAt;
    private T dtoObject;
    private String message;


    public static <T> DTOResponse<T> success(int status, T data) {
        return new DTOResponse<>(status, Instant.now(), data, null);
    }

    public static <T> DTOResponse<T> error(int status, String errorMessage) {
        return new DTOResponse<>(status, Instant.now(), null, errorMessage);
    }
}
