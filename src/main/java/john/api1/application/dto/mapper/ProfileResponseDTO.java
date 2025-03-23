package john.api1.application.dto.mapper;

import java.time.Instant;

public record ProfileResponseDTO(String id, String url, Instant expiredAt) {
}
