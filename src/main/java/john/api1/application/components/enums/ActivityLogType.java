package john.api1.application.components.enums;

import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityLogType {
    PET_MANAGEMENT("PET_MANAGEMENT", "Pet Management"),
    PET_OWNER_MANAGEMENT("PET_OWNER_MANAGEMENT", "Pet Owner Management"),
    BOARDING_MANAGEMENT("BOARDING_MANAGEMENT", "Boarding Management"),
    REQUEST_MANAGEMENT("REQUEST_MANAGEMENT", "Request Management");

    private final String activityLogType;
    private final String activityLogTypeToDTO;

    public static ActivityLogType fromString(String type) {
        for (ActivityLogType t : ActivityLogType.values()) {
            if (t.activityLogType.equalsIgnoreCase(type)) {
                return t;
            }
        }
        throw new DomainArgumentException("Unknown ActivityLogType: " + type);
    }
}
