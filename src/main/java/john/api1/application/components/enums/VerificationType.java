package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VerificationType {
    VERIFICATION_LINK("VerificationLink"),
    VERIFICATION_CODE("VerificationCode");

    private final String verificationType;

    public static VerificationType fromString(String value) {
        for (VerificationType type : VerificationType.values()) {
            if (type.getVerificationType().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return VERIFICATION_LINK; // Default fallback value
    }
}
