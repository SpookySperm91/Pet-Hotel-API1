package john.api1.application.dto.mapper;

import java.time.Instant;

public record ProfileDTO(String id, String url, Instant expiredAt) {
}
