package john.api1.application.components.annotation.notification;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotificationTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNotificationType {
    String message() default "Invalid notification type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
