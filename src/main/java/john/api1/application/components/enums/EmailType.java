package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmailType {
    REGISTERED("Registered"),
    RESET_PASSWORD_LINK("ResetPasswordLink"),
    RESET_PASSWORD_CODE("ResetPasswordCode");

    private final String emailType;
}
