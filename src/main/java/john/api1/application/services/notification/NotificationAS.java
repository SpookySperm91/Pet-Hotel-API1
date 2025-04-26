package john.api1.application.services.notification;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.NotificationDTO;
import john.api1.application.dto.request.NotificationRDTO;
import john.api1.application.ports.repositories.notification.INotificationCreateRepository;
import john.api1.application.ports.repositories.notification.INotificationManageRepository;
import john.api1.application.ports.services.notification.INotificationCreate;
import john.api1.application.ports.services.notification.INotificationDelete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NotificationAS implements INotificationCreate, INotificationDelete {
    private final INotificationCreateRepository createRepository;
    private final INotificationManageRepository deleteRepository;

    @Autowired
    public NotificationAS(INotificationCreateRepository createRepository,
                          INotificationManageRepository deleteRepository) {
        this.createRepository = createRepository;
        this.deleteRepository = deleteRepository;
    }

    // Create
    @Override
    public DomainResponse<NotificationDTO> createNotification(NotificationRDTO notification) {
        return DomainResponse.success();
    }


    // Delete
    @Override
    public DomainResponse<Void> deleteById(String id) {
        return DomainResponse.success();

    }

    @Override
    public DomainResponse<Void> deleteAllRead(String ownerId) {
        return DomainResponse.success();

    }

    @Override
    public DomainResponse<Void> deleteAllByDay(String ownerId, Instant day) {
        return DomainResponse.success();

    }
}
