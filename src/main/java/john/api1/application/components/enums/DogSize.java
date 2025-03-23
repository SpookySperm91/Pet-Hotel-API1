package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DogSize {
    SMALL("SMALL", 25, 300, 500),
    MEDIUM("MEDIUM", 30,400, 650),
    LARGE("LARGE", 40, 500, 800),
    XL("XL", 50, 600, 950);

    private final String getPetSize;
    private final double getBoardingPrice;
    private final double getBasicGroomingPrice;
    private final double getFullGroomingPrice;
}

