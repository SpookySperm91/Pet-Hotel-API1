package john.api1.application.ports.repositories.notification;

import john.api1.application.domain.models.request.NotificationDomain;

import java.util.Optional;

public interface INotificationCreateRepository {
    Optional<String> save(NotificationDomain domain);
}
