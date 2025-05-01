package john.api1.application.async;

import john.api1.application.dto.request.NotificationRDTO;
import john.api1.application.ports.services.notification.INotificationCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncNotificationService {
    private final INotificationCreate notificationCreate;

    @Autowired
    public AsyncNotificationService(INotificationCreate notificationCreate) {
        this.notificationCreate = notificationCreate;
    }

    @Async
    public void createNotification(NotificationRDTO dto) {
        notificationCreate.createNotification(dto);
    }


}