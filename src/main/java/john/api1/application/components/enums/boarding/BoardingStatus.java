package john.api1.application.components.enums.boarding;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum BoardingStatus {
    BOARDING("BOARDING"),
    DONE_BOARDING("DONE_BOARDING"),
    OVERDUE("OVERDUE"),
    RELEASED("RELEASED");

    private final String boardingStatus;

    public static BoardingStatus fromStringOrDefault(String dbValue) {
        return Arrays.stream(BoardingStatus.values())
                .filter(bt -> bt.boardingStatus.equalsIgnoreCase(dbValue))
                .findFirst()
                .orElse(BoardingStatus.BOARDING);
    }
}
