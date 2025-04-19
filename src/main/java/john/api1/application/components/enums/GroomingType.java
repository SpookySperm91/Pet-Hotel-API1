package john.api1.application.components.enums;

import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum GroomingType {
    BASIC_WASH("BASIC_WASH", "Basic Wash"),
    FULL_GROOMING("FULL_GROOMING", "Full Wash");

    private final String groomingType;
    private final String groomingTypeToDTO;


    public static GroomingType safeFromStringOrDefault(String dbValue) {
        return Arrays.stream(GroomingType.values())
                .filter(bt -> bt.groomingType.equalsIgnoreCase(dbValue))
                .findFirst()
                .orElseThrow(() -> new DomainArgumentException("Invalid grooming type: '" + dbValue + "'. Valid statuses are: 'BASIC_WASH' or 'FULL_GROOMING'"));
    }

    public static GroomingType safeFromStringOrNull(String dbValue) {
        return Arrays.stream(GroomingType.values())
                .filter(bt -> bt.groomingType.equalsIgnoreCase(dbValue))
                .findFirst()
                .orElse(null);
    }

}
