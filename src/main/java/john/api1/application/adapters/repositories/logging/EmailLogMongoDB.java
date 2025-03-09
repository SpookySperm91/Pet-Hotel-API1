package john.api1.application.adapters.repositories.logging;

import john.api1.application.adapters.repositories.EmailLogsEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.EmailLogsDomain;
import john.api1.application.ports.repositories.ILogEmailRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("MongoEmailLogRepo")
public class EmailLogMongoDB implements ILogEmailRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public EmailLogMongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public void logEmail(EmailLogsDomain email) throws PersistenceException {
        EmailLogsEntity emailLog = new EmailLogsEntity(
                null,
                ObjectId.isValid(email.getOwnerId()) ? new ObjectId(email.getOwnerId()) : null,
                email.getRecipientEmail(),
                email.getRecipientUsername(),
                email.getEmailType().getEmailType(),
                email.getBody(),
                email.getStatus().getResponseStatus(),
                email.getErrorReason(),
                email.getSendAt(),
                email.getUpdatedAt()
        );
        mongoTemplate.save(emailLog);
    }
}
