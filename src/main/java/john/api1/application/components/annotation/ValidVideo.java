package john.api1.application.components.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VideoValidator.class)
public @interface ValidVideo {
    String message() default "Invalid video file format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
