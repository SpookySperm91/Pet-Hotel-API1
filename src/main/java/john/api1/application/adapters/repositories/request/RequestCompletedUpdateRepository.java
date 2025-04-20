package john.api1.application.adapters.repositories.request;

import john.api1.application.adapters.repositories.ExtensionEntity;
import john.api1.application.adapters.repositories.GroomingEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;
import john.api1.application.ports.repositories.request.IRequestCompletedUpdateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class RequestCompletedUpdateRepository implements IRequestCompletedUpdateRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestCompletedUpdateRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void updateApprovalExtension(String id, boolean approval, Instant updatedAt) {
        validateId(id);

        Query query = new Query(where("_id").is(new ObjectId(id)));
        Update update = new Update()
                .set("approved", approval)
                .set("updatedAt", updatedAt);
        mongoTemplate.updateFirst(query, update, ExtensionEntity.class);
    }

    @Override
    public void updateApprovalGrooming(String id, boolean approval, Instant updatedAt) {
        validateId(id);

        Query query = new Query(where("_id").is(new ObjectId(id)));
        Update update = new Update()
                .set("approved", approval)
                .set("updatedAt", updatedAt);
        mongoTemplate.updateFirst(query, update, GroomingEntity.class);
    }

    @Override
    public void updateExtension(ExtensionDomain domain) {
        Query query = new Query(where("_id").is(domain.getId()));
        Update update = new Update()
                .set("boardingId", domain.getBoardingId())
                .set("additionalPrice", domain.getAdditionalPrice())
                .set("extendedHours", domain.getExtendedHours())
                .set("description", domain.getDescription())
                .set("approved", domain.isApproved())
                .set("updatedAt", domain.getUpdatedAt());
        mongoTemplate.updateFirst(query, update, ExtensionEntity.class);
    }

    @Override
    public void updateGrooming(GroomingDomain domain) {
        Query query = new Query(where("_id").is(domain.getId()));
        Update update = new Update()
                .set("boardingId", domain.getBoardingId())
                .set("groomingType", domain.getGroomingType())
                .set("groomingPrice", domain.getGroomingPrice())
                .set("description", domain.getDescription())
                .set("approved", domain.isApproved())
                .set("updatedAt", domain.getUpdatedAt());
        mongoTemplate.updateFirst(query, update, GroomingEntity.class);
    }

    private void validateId(String id) {
        if(!ObjectId.isValid(id)) throw new PersistenceException("Invalid id cannot be converted to ObjectId!");
    }

}
