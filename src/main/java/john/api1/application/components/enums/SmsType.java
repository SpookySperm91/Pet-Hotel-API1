package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SmsType {
    REGISTER("Register"),
    CHANGE_PASSWORD_CODE("ChangePasswordCode"),
    UPDATE_STATUS("UpdateStatus"),
    NOTIFICATION("Notification");

    private final String SmsType;

}
