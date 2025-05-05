package john.api1.application.adapters.repositories.history;

import john.api1.application.adapters.repositories.ActivityLogEntity;
import john.api1.application.components.enums.ActivityLogType;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.ActivityLogDomain;
import john.api1.application.ports.repositories.history.IHistoryLogSearchRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class HistoryLogSearchRepository implements IHistoryLogSearchRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public HistoryLogSearchRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<ActivityLogDomain> searchById(String id) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid activity-id, cannot be converted to ObjectId");

        return Optional.ofNullable(mongoTemplate.findById(new ObjectId(id), ActivityLogEntity.class))
                .map(this::mapToDomain);
    }

    @Override
    public Optional<ActivityLogDomain> searchRecently() {
        var query = new org.springframework.data.mongodb.core.query.Query()
                .with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
                .limit(1);

        return Optional.ofNullable(mongoTemplate.findOne(query, ActivityLogEntity.class))
                .map(this::mapToDomain);
    }

    @Override
    public List<ActivityLogDomain> searchAll() {
        // Fetch all records and convert to domain objects
        return mongoTemplate.findAll(ActivityLogEntity.class).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogDomain> searchByDate(Instant date) {
        // Adjust to midnight for date comparison
        var start = date.truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        var end = start.plus(java.time.Duration.ofDays(1));

        var query = new org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("createdAt").gte(start).lt(end)
        );

        // Find all logs for the given date range
        return mongoTemplate.find(query, ActivityLogEntity.class).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogDomain> searchByActivityType(ActivityLogType type) {
        // Find all logs for the given activity type
        var query = new org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("activityType").is(type.getActivityLogType())
        );

        return mongoTemplate.find(query, ActivityLogEntity.class).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }


    private ActivityLogDomain mapToDomain(ActivityLogEntity entity) {
        return new ActivityLogDomain(
                entity.getId().toHexString(),
                entity.getTypeId() != null ? entity.getTypeId().toHexString(): null,
                ActivityLogType.fromString(entity.getActivityType()),
                RequestType.fromString(entity.getRequestType()),
                entity.getPerformedBy(),
                entity.getPetOwner(),
                entity.getPet(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }

}
