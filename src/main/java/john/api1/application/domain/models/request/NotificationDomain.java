package john.api1.application.domain.models.request;

import jakarta.annotation.Nullable;
import john.api1.application.components.enums.NotificationType;
import john.api1.application.components.exception.PersistenceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class NotificationDomain {
    private final String id;
    @Nullable
    private final String requestId;
    private final String ownerId;
    private final String description;
    private final NotificationType notificationType;
    private final Instant createdAt;
    private boolean read = false;  // read/unread


    public static NotificationDomain create(String requestId, String ownerId, String description, NotificationType notificationType) {
        if (!ObjectId.isValid(requestId))
            throw new PersistenceException("Invalid request id cannot be converted to ObjectId");
        if (!ObjectId.isValid(ownerId))
            throw new PersistenceException("Invalid owner id cannot be converted to ObjectId");

        return new NotificationDomain(null, requestId, ownerId, description, notificationType, Instant.now(), false);
    }

    public static NotificationDomain create(String ownerId, String description, NotificationType notificationType) {
        if (!ObjectId.isValid(ownerId))
            throw new PersistenceException("Invalid owner id cannot be converted to ObjectId");

        return new NotificationDomain(null, null, ownerId, description, notificationType, Instant.now(), false);
    }

    public static NotificationDomain map(String id, String requestId, String ownerId, String description, NotificationType notificationType, Instant createdAt, boolean read) {
        return new NotificationDomain(id, requestId, ownerId, description, notificationType, createdAt, read);
    }

    public static NotificationDomain mapWithId(String id, NotificationDomain domain) {
        return new NotificationDomain(id, domain.getRequestId(), domain.ownerId, domain.getDescription(), domain.getNotificationType(), domain.getCreatedAt(), domain.read);
    }


}