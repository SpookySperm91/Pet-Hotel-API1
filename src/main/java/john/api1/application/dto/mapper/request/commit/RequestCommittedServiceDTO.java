package john.api1.application.dto.mapper.request.commit;

import java.time.Instant;

public record RequestCommittedServiceDTO(
        String ownerId,
        String requestId,
        String requestType,
        String status,
        Instant completedAt
) {
}
