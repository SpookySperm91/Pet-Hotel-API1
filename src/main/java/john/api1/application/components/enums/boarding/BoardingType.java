package john.api1.application.components.enums.boarding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BoardingType {
    DAYCARE("DAYCARE"),
    LONG_STAY("LONG_STAY");

    private final String boardingType;
}
