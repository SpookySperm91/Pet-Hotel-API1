package john.api1.application.dto.mapper.history;

import java.time.Instant;

public record ActivityLogPetRegisterDTO(
        String id,
        String activityType,
        String description,
        String performedBy,
        Instant timestamp,
        // Pet information
        String petName,
        String type,
        String breed,
        String size,
        // Owner
        String ownerName
) implements ActivityLogDTO {
}
