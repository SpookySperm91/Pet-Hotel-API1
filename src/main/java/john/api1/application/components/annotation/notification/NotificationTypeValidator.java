package john.api1.application.components.annotation.notification;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import john.api1.application.components.enums.NotificationType;

public class NotificationTypeValidator implements ConstraintValidator<ValidNotificationType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // If nullable is allowed, treat null as valid
        }

        for (NotificationType type : NotificationType.values()) {
            if (type.getNotificationType().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
