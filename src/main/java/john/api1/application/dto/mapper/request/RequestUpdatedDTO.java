package john.api1.application.dto.mapper.request;

import java.time.Instant;

public record RequestUpdatedDTO(
        // id
        String id,
        String ownerId,
        String petId,
        String boardingId,

        // information
        String requestType,
        String requestStatus,
        String description,
        Instant requestAt,
        Instant updatedAt,
        String rejectionMessage,
        boolean active
) {
}
