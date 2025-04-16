package john.api1.application.components.anoitation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhotoValidator.class)
public @interface ValidPhoto {
    String message() default "Invalid photo file";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}