package john.api1.application.async;

import john.api1.application.dto.request.NotificationRDTO;
import john.api1.application.services.notification.NotificationAS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AsyncNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncNotificationService.class);
    private final NotificationAS notificationAS;

    public AsyncNotificationService(NotificationAS notificationAS) {
        this.notificationAS = notificationAS;
    }

    public void tryCreate(NotificationRDTO dto) {
        try {
            var result = notificationAS.createNotification(dto);
            if (!result.isSuccess()) {
                LOGGER.warn("Notification not created: {}", result.getMessage());
            }
        } catch (Exception ex) {
            LOGGER.error("Unexpected error creating notification", ex);
        }
    }

}