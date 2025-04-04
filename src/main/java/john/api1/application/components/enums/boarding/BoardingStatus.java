package john.api1.application.components.enums.boarding;

import john.api1.application.components.exception.DomainArgumentException;
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

    public static BoardingStatus safeFromStringOrDefault(String dbValue) {
        return Arrays.stream(BoardingStatus.values())
                .filter(bt -> bt.boardingStatus.equalsIgnoreCase(dbValue))
                .findFirst()
                .orElseThrow(() -> new DomainArgumentException("Invalid boarding status: '" + dbValue + "'. Valid statuses are: 'BOARDING', 'DONE_BOARDING', 'OVERDUE', 'RELEASED'"));
    }
}
