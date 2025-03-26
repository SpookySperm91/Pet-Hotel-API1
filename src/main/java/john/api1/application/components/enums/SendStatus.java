package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SendStatus {
    PENDING("Pending"),
    SUCCESS("Success"),
    FAILED("Failed"),

    // verification
    EXPIRED("Expired"),
    INVALID("Invalid"),
    VALID("Valid");

    private final String responseStatus;
}
