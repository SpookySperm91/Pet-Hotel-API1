package john.api1.application.components.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VideoValidator implements ConstraintValidator<ValidVideo, String> {

    @Override
    public void initialize(ValidVideo constraintAnnotation) {
        // No initialization needed for now
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        String[] validExtensions = {".mp4", ".avi", ".mov"};
        for (String ext : validExtensions) {
            if (value.toLowerCase().endsWith(ext)) {
                return true;
            }
        }

        return false;
    }
}