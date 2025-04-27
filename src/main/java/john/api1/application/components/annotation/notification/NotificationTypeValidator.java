package john.api1.application.components.annotation.notification;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import john.api1.application.components.enums.NotificationType;

import java.util.Arrays;

public class NotificationTypeValidator implements ConstraintValidator<ValidNotificationType, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Arrays.stream(NotificationType.values())
                .anyMatch(type -> type.getNotificationType().equals(value));
    }
}
