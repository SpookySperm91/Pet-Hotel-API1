package john.api1.application.dto.mapper.request.commit;

import java.time.Instant;

public record RequestCommittedVideoDTO(
        String id,
        String requestId,
        String ownerId,
        String mediaId,
        String url,
        Instant createdAt
) {
}
