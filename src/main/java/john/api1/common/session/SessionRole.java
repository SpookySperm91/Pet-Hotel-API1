package john.api1.common.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SessionRole {
    PET_OWNER("PET_OWNER"),
    ADMIN("ADMIN");

    private final String sessionRole;

    public static SessionRole fromValue(String value) {
        for (SessionRole role : values()) {
            if (role.getSessionRole().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
