package john.api1.application.ports.repositories.notification;

import john.api1.application.domain.models.request.NotificationDomain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface INotificationManageRepository {
    // Search
    Optional<NotificationDomain> searchById(String id);
    Optional<NotificationDomain> searchRecentByOwner(String ownerId);
    List<NotificationDomain> searchAllByOwner(String ownerId);

    // Delete
    void deleteById(String id);
    void deleteAllRead(String ownerId);
    void deleteAllByDay(String ownerId, Instant day);

    // Test
    List<NotificationDomain> searchAll();
}
