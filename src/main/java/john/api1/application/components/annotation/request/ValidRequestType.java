package john.api1.application.components.annotation.request;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RequestTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRequestType {
    String message() default "Invalid request type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
