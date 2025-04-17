package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityLogType {
    PET_MANAGEMENT("PET_MANAGEMENT"),
    PET_OWNER_MANAGEMENT("PET_OWNER_MANAGEMENT"),
    BOARDING_MANAGEMENT("BOARDING_MANAGEMENT"),
    REQUEST_MANAGEMENT("REQUEST_MANAGEMENT"),
    REQUEST("REQUEST");

    private final String activityLogType;
}
