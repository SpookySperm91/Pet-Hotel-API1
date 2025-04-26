package john.api1.application.ports.services.notification;

import john.api1.application.components.DomainResponse;
import john.api1.application.domain.models.request.NotificationDomain;
import john.api1.application.dto.mapper.NotificationDTO;

import java.util.List;

public interface INotificationSearch {
    DomainResponse<NotificationDTO> searchById(String id);

    DomainResponse<NotificationDTO> searchOwnerRecent(String ownerId);

    DomainResponse<List<NotificationDTO>> searchAllByOwner(String ownerId);
}
