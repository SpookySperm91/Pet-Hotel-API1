package john.api1.application.dto.mapper.request;


import java.time.Instant;

public record RequestMediaCreatedDTO(
        // id
        String id,
        String ownerId,
        String petId,
        String boardingId,
        // information
        String ownerName,
        String petName,
        String requestType,
        String requestStatus,
        String description,
        Instant requestAt
) {
}

