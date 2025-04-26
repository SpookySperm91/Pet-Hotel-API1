package john.api1.application.dto.mapper;

import java.time.Instant;

public record NotificationDTO(
        String id,
        String ownerId,
        String requestId,
        String notificationType,
        String notificationTitle,
        String description,
        Instant date
) {
}
