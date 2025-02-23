package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmailType {
    REGISTERED("Registered"),
    RESET_PASSWORD("ResetPassword");

    private String emailType;
}
