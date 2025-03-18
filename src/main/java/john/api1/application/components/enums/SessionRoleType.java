package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SessionRoleType {
    ADMIN("ADMIN"),
    USER("USER"),
    ONE_TIME("ONE_TIME");

    private final String SessionRole;
}
