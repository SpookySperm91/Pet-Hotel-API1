package john.api1.application.services.request;

import john.api1.application.components.exception.PersistenceException;
import john.api1.application.ports.repositories.request.IRequestScheduledUpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RequestScheduleAS {
    private static final Logger logger = LoggerFactory.getLogger(RequestScheduleAS.class);
    private final IRequestScheduledUpdateRepository scheduledUpdate;

    @Autowired
    public RequestScheduleAS(IRequestScheduledUpdateRepository scheduledUpdate) {
        this.scheduledUpdate = scheduledUpdate;
    }

    // Scheduled every 5 minutes
    // Update rejected request as inactive after 5 minutes
    @Scheduled(fixedDelay = 300000)
    public void markRejectedAsInactiveAfterFiveMinutes() {
        try {
            var updated = scheduledUpdate.markRejectedAsInactiveAfterFiveMinutes();
            String formattedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm a"));
            logger.info("[{}] {} rejected request(s) updated to inactive.", formattedTime, updated);
        } catch (PersistenceException e) {
            logger.info(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while marking rejected requests as inactive: {}", e.getMessage(), e);
        }
    }
}
