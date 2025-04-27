package john.api1.application.dto.mapper;

import john.api1.application.domain.models.request.NotificationDomain;

import java.time.Instant;

public record NotificationDTO(
        String id,
        String ownerId,
        String requestId,
        String notificationTitle,
        String description,
        Instant date
) {

    public static NotificationDTO map(NotificationDomain domain) {
        return new NotificationDTO(
                domain.getId(),
                domain.getOwnerId(),
                domain.getRequestId(),
                domain.getNotificationType().getNotificationTypeDto(),
                domain.getDescription(),
                domain.getCreatedAt());
    }
}
