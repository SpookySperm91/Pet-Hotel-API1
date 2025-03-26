package john.api1.application.components.enums;

public enum PetSize {
    // DOGS
    SMALL, MEDIUM, LARGE, XL, 
    // CATS
    KITTEN, ADULT;



    public boolean isDogSize() {
        return this == SMALL || this == MEDIUM || this == LARGE || this == XL;
    }

    public boolean isCatSize() {
        return this == KITTEN || this == ADULT;
    }
}
