package john.api1.application.adapters.repositories.request;

import john.api1.application.adapters.repositories.RequestEntity;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.ports.repositories.request.IRequestScheduledUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
public class RequestScheduledUpdateRepository implements IRequestScheduledUpdateRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestScheduledUpdateRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public int markRejectedAsInactiveAfterFiveMinutes() {
        Instant now = Instant.now();
        Query query = new Query(Criteria.where("requestStatus").is(RequestStatus.REJECTED)
                .and("resolvedTime").lt(now.minus(5, ChronoUnit.MINUTES)));
        Update update = new Update().set("active", false);

        var result = mongoTemplate.updateMulti(query, update, RequestEntity.class);
        if (result.getModifiedCount() == 0) {
            throw new PersistenceException("No requests were marked as inactive.");
        }
        return (int) result.getModifiedCount();
    }

}
