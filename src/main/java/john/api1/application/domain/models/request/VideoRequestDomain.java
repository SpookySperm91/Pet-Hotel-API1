package john.api1.application.domain.models.request;

import java.time.Instant;


public record VideoRequestDomain(
        String id,
        String requestId,
        String ownerId,
        String mediaId,
        String videoName,
        Instant uploadedAt) {
}
