package john.api1.application.components.annotation.request;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import john.api1.application.components.enums.boarding.RequestType;

import java.util.Arrays;

public class RequestTypeValidator implements ConstraintValidator<ValidRequestType, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Arrays.stream(RequestType.values())
                .anyMatch(type -> type.getRequestType().equals(value));
    }

}
