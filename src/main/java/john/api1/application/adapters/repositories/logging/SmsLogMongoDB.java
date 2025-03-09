package john.api1.application.adapters.repositories.logging;

import john.api1.application.adapters.repositories.SmsLogEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.SmsLogDomain;
import john.api1.application.ports.repositories.ILogSmsRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("MongoSmsLogRepo")
public class SmsLogMongoDB implements ILogSmsRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public SmsLogMongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Qualifier
    public void logSmsText(SmsLogDomain sms) throws PersistenceException {
        SmsLogEntity logEntity = new SmsLogEntity(
                null,
                ObjectId.isValid(sms.getOwnerId()) ? new ObjectId(sms.getOwnerId()) : null,
                sms.getUsername(),
                sms.getPhoneNumber(),
                sms.getSmsType().getSmsType(),
                sms.getBody(),
                sms.getStatus().getResponseStatus(),
                sms.getStatusReason(),
                sms.getSendAt(),
                sms.getUpdatedAt()
        );

        mongoTemplate.save(logEntity);
    }
}
