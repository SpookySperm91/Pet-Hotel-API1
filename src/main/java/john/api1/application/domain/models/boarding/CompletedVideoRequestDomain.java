package john.api1.application.domain.models.boarding;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;


public record CompletedVideoRequestDomain(
        String id,
        String requestId,
        String ownerId,
        String mediaId,
        String videoName,
        Instant uploadedAt) {
}
