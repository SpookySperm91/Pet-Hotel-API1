package john.api1.application.ports.services.notification;

import john.api1.application.components.DomainResponse;

import java.time.Instant;

public interface INotificationDelete {
    DomainResponse<Void> deleteById(String id);

    DomainResponse<Void> deleteAllRead(String ownerId);

    DomainResponse<Void> deleteAllByDay(String ownerId, Instant day);
}
