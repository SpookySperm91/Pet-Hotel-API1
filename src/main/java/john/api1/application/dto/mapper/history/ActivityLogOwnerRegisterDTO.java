package john.api1.application.dto.mapper.history;

import john.api1.application.components.DateUtils;

import java.time.Instant;

public record ActivityLogOwnerRegisterDTO(
        String id,
        String activityType,
        String description,
        String performedBy,
        String timestamp,
        // Owner
        String ownerName,
        String email,
        String phoneNumber,
        String address

) implements ActivityLogDTO{
}
