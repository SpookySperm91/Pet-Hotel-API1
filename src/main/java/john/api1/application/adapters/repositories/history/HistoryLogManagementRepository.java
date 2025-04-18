package john.api1.application.adapters.repositories.history;

import john.api1.application.adapters.repositories.ActivityLogEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.ActivityLogDomain;
import john.api1.application.ports.repositories.history.IHistoryLogManageRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
public class HistoryLogManagementRepository implements IHistoryLogManageRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public HistoryLogManagementRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void deleteById(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid activity-log id cannot be converted to ObjectId");

        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        var result = mongoTemplate.remove(query, ActivityLogEntity.class);
        if (result.getDeletedCount() == 0) {
            throw new PersistenceException("No activity log found to delete for id: " + id);
        }
    }


    @Override
    public void deleteByDate(Instant date) {
        Instant start = date.truncatedTo(ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.DAYS);

        Query query = new Query(Criteria.where("createdAt").gte(start).lt(end));
        var result = mongoTemplate.remove(query, ActivityLogEntity.class);
        if (result.getDeletedCount() == 0) {
            throw new PersistenceException("No activity logs found to delete for date: " + date);
        }
    }


    // ???
    @Override
    public void updateFull(ActivityLogDomain domain) {
        if (!ObjectId.isValid(domain.getId())) throw new PersistenceException("Invalid activity-log id cannot be converted to ObjectId");

        var existing = mongoTemplate.findById(new ObjectId(domain.getPet()), ActivityLogEntity.class);
        if (existing == null) {
            throw new PersistenceException("No activity log found to update for id: " + domain.getId());
        }

        ActivityLogEntity entity = new ActivityLogEntity();
        entity.setId(new ObjectId(domain.getId()));
        entity.setTypeId(new ObjectId(domain.getTypeId()));
        entity.setActivityType(domain.getActivityType().getActivityLogType());
        entity.setPerformedBy(domain.getDescription());
        entity.setPetOwner(domain.getPetOwner());
        entity.setPet(domain.getPet());
        entity.setDescription(domain.getDescription());
        entity.setCreatedAt(domain.getTimestamp());

        mongoTemplate.save(entity);
    }

}
