package john.api1.application.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class TokenDomain {
    private final String token;
    private final String authorizedId;
    private final String endpoint;
    private final Instant createdAt;
    private final Instant expiredAt;
    @Setter
    private boolean used;

    public static TokenDomain copyOf(TokenDomain token) {
        return new TokenDomain(
                token.token,
                token.authorizedId,
                token.endpoint,
                token.createdAt,
                token.expiredAt,
                token.used
        );
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiredAt);
    }

    public boolean isValid(String endpoint, String userId) {
        return !isExpired() && !used && this.endpoint.equals(endpoint) && this.authorizedId.equals(userId);
    }

    public void markAsUsed() {
        this.used = true;
    }
}
