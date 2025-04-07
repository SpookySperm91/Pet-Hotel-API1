package john.api1.application.ports.repositories.wrapper;

import java.time.Instant;

public record MediaIdUrlExpire(String id, String mediaUrl, Instant expireAt) {
}
