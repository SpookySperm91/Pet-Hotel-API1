package john.api1.application.adapters.repositories.logging;

import com.mongodb.MongoException;
import john.api1.application.adapters.repositories.LogFailedEmailEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.ports.repositories.ILoggingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@Qualifier("MongoLoggingRepo")
public class LoggingMongoDB implements ILoggingRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public LoggingMongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void logFailedEmail(String recipientEmail, String recipientUsername, String emailType, String body, String errorMessage) {
        try {
            LogFailedEmailEntity failedEmail = new LogFailedEmailEntity(
                    null,
                    recipientEmail,
                    recipientUsername,
                    emailType,
                    body,
                    Instant.now(),
                    Instant.now(),
                    false,
                    errorMessage
            );

            mongoTemplate.save(failedEmail);
        } catch (MongoException e) {
            throw new PersistenceException("MongoDB Database error: Failed to save failed email sent log to database", e);
        }
    }

}
