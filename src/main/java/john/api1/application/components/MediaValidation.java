package john.api1.application.components;

import john.api1.application.components.exception.DomainArgumentException;
import org.springframework.web.multipart.MultipartFile;

public class MediaValidation {
    public static void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DomainArgumentException("File cannot be empty.");
        }

        String filename = file.getOriginalFilename();
        if (!filename.matches(".*\\.(jpg|jpeg|png|webp)$")) {
            throw new DomainArgumentException("Only JPG, JPEG, PNG, or WEBP images are allowed.");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new DomainArgumentException("File size must be less than 5MB.");
        }
    }
}
