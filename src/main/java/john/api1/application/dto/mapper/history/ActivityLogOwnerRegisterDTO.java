package john.api1.application.dto.mapper.history;

import java.time.Instant;

public record ActivityLogOwnerRegisterDTO(
        String id,
        String activityType,
        String description,
        String performedBy,
        Instant timestamp,
        // Owner
        String ownerName,
        String email,
        String phoneNumber,
        String address
) implements ActivityLogDTO{
}
