package john.api1.application.adapters.repositories.notification;

import com.mongodb.client.result.DeleteResult;
import john.api1.application.adapters.repositories.NotificationEntity;
import john.api1.application.components.enums.NotificationType;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.NotificationDomain;
import john.api1.application.ports.repositories.notification.INotificationCreateRepository;
import john.api1.application.ports.repositories.notification.INotificationManageRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository implements INotificationCreateRepository, INotificationManageRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public NotificationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<String> save(NotificationDomain domain) {
        if (domain.getRequestId() != null && !ObjectId.isValid(domain.getRequestId()))
            throw new PersistenceException("Invalid request id cannot be converted to ObjectId");
        validateId(domain.getOwnerId(), "owner");

        NotificationEntity entity = new NotificationEntity();
        entity.setRequestId(domain.getRequestId() != null ? new ObjectId(domain.getRequestId()) : null);
        entity.setOwnerId(new ObjectId(domain.getOwnerId()));
        entity.setDescription(domain.getDescription());
        entity.setNotificationType(domain.getNotificationType().getNotificationType());
        entity.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : Instant.now());
        entity.setRead(domain.isRead());

        NotificationEntity saved = mongoTemplate.save(entity);
        return Optional.ofNullable(saved.getId()).map(ObjectId::toString);
    }

    // Management


    // Search
    @Override
    public Optional<NotificationDomain> searchById(String id) {
        validateId(id, "");

        NotificationEntity entity = mongoTemplate.findById(new ObjectId(id), NotificationEntity.class);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<NotificationDomain> searchRecentByOwner(String ownerId) {
        validateId(ownerId, "owner");

        Query query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(new ObjectId(ownerId)));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        query.limit(1);

        NotificationEntity entity = mongoTemplate.findOne(query, NotificationEntity.class);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<NotificationDomain> searchAllByOwner(String ownerId) {
        validateId(ownerId, "owner");

        Query query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(new ObjectId(ownerId)));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        List<NotificationEntity> entities = mongoTemplate.find(query, NotificationEntity.class);
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        validateId(id, "notification");

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        DeleteResult result = mongoTemplate.remove(query, NotificationEntity.class);

        // Check if any document was deleted
        if (result.getDeletedCount() == 0) {
            throw new PersistenceException("No notification found with the given id: " + id);
        }
    }

    @Override
    public long deleteAllRead(String ownerId) {
        validateId(ownerId, "owner");

        Query query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(new ObjectId(ownerId)).and("read").is(true));
        DeleteResult result = mongoTemplate.remove(query, NotificationEntity.class);

        // Check if any document was deleted
        if (result.getDeletedCount() == 0) {
            throw new PersistenceException("No read notifications found for the owner with id: " + ownerId);
        }
        return result.getDeletedCount();
    }

    @Override
    public long deleteAllByDay(String ownerId, Instant day) {
        validateId(ownerId, "owner");

        Instant startOfDay = day.truncatedTo(ChronoUnit.DAYS);
        Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

        Query query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(new ObjectId(ownerId))
                .andOperator(
                        Criteria.where("createdAt").gte(startOfDay),
                        Criteria.where("createdAt").lt(endOfDay)
                )
        );
        DeleteResult result = mongoTemplate.remove(query, NotificationEntity.class);

        // Check if any document was deleted
        if (result.getDeletedCount() == 0) {
            throw new PersistenceException("No notifications found for the owner with id: " + ownerId + " on the specified day.");
        }
        return result.getDeletedCount();
    }


    // Test
    @Override
    public List<NotificationDomain> searchAll() {
        List<NotificationEntity> entities = mongoTemplate.findAll(NotificationEntity.class);
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    private NotificationDomain toDomain(NotificationEntity entity) {
        return NotificationDomain.map(
                entity.getId().toString(),
                entity.getRequestId() != null ? entity.getRequestId().toString() : null,
                entity.getOwnerId().toString(),
                entity.getDescription(),
                NotificationType.fromString(entity.getNotificationType()),
                entity.getCreatedAt(),
                entity.isRead()
        );
    }

    private void validateId(String id, String type) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid " + type + " id cannot be converted to ObjectId");
    }
}
