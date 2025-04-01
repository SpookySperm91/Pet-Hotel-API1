package john.api1.application.components.enums;

import john.api1.application.components.exception.DomainArgumentException;

public enum PetSize {
    // DOGS
    SMALL, MEDIUM, LARGE, XL,
    // CATS
    KITTEN, ADULT;

    public static PetSize fromStringToSize(String size) {
        if (size == null || size.trim().isEmpty()) {
            throw new DomainArgumentException("Pet size type cannot be empty");
        }

        return switch (size.trim().toUpperCase()) {
            case "SMALL" -> PetSize.SMALL;
            case "MEDIUM" -> PetSize.MEDIUM;
            case "LARGE" -> PetSize.LARGE;
            case "XL" -> PetSize.XL;
            case "KITTEN" -> PetSize.KITTEN;
            case "ADULT" -> PetSize.ADULT;
            default -> throw new DomainArgumentException("Invalid size type: " + size);
        };
    }

    public boolean isDogSize() {
        return this == SMALL || this == MEDIUM || this == LARGE || this == XL;
    }

    public boolean isCatSize() {
        return this == KITTEN || this == ADULT;
    }
}
