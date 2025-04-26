package john.api1.application.ports.services.notification;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.NotificationDTO;
import john.api1.application.dto.request.NotificationRDTO;

public interface INotificationCreate {
    DomainResponse<NotificationDTO> createNotification(NotificationRDTO notification);
}
