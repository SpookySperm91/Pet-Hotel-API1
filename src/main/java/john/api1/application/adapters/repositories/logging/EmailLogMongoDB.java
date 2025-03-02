package john.api1.application.adapters.repositories.logging;

import john.api1.application.adapters.repositories.EmailLogsEntity;
import john.api1.application.domain.models.EmailLogsDomain;
import john.api1.application.ports.repositories.ILogEmailRepository;
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
    public void logEmail(EmailLogsDomain email){
        EmailLogsEntity emailLog = new EmailLogsEntity(
                null,
                email.getRecipientEmail(),
                email.getRecipientUsername(),
                email.getEmailType().getEmailType(),
                email.getBody(),
                email.getStatus().getEmailStatus(),
                email.getErrorReason(),
                email.getSendAt(),
                email.getUpdatedAt()
        );
        mongoTemplate.save(emailLog);
    }
}
