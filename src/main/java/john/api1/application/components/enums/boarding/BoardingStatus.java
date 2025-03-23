package john.api1.application.components.enums.boarding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BoardingStatus {
    BOARDING("BOARDING"),
    DONE_BOARDING("DONE_BOARDING"),
    RELEASED("RELEASED");

    private final String boardingStatus;
}
