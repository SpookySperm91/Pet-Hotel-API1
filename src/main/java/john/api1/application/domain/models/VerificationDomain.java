package john.api1.application.domain.models;

import john.api1.application.components.enums.VerificationType;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VerificationDomain {
    private String id;
    private String associatedId;
    private String associatedUsername;
    private VerificationType type;
    private String data;
    private boolean used;
    private Instant createdAt;
    private Instant expiredAt;

    public static VerificationDomain createNew(VerificationType type, String associateId, String username, String data, int minutes) {
        return new VerificationDomain(null, associateId, username, type, data, false, Instant.now(), Instant.now().plus(Duration.ofMinutes(minutes)));
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiredAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    public VerificationDomain markAsUsed() {
        if (isExpired()) {
            throw new DomainArgumentException("Cannot mark an expired verification as used.");
        }
        return new VerificationDomain(id, associatedId, associatedUsername, type, data, true, createdAt, expiredAt);
    }
}
