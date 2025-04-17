package john.api1.application.components.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhotoValidator implements ConstraintValidator<ValidPhoto, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            context.buildConstraintViolationWithTemplate("Filename cannot be empty").addConstraintViolation();
            return false; // Reject null or empty filenames
        }

        // Add logic for checking valid photo extensions
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        for (String ext : validExtensions) {
            if (value.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false; // Not a valid photo file extension
    }
}

