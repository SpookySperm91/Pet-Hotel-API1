package john.api1.application.adapters.repositories.verification;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import john.api1.application.adapters.repositories.VerificationEntity;
import john.api1.application.components.enums.VerificationType;
import john.api1.application.domain.models.VerificationDomain;
import john.api1.application.ports.repositories.IVerificationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@Qualifier("MongoVerificationRepo")
public class VerificationRepositoryMongo implements IVerificationRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public VerificationRepositoryMongo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // Save new to db
    public String save(VerificationDomain verification) {
        ObjectId associatedObjectId = ObjectId.isValid(verification.getAssociatedId())
                ? new ObjectId(verification.getAssociatedId())
                : null;

        VerificationEntity entity = new VerificationEntity(
                null,
                associatedObjectId,
                verification.getAssociatedUsername(),
                verification.getType().getVerificationType(),
                verification.getData(),
                verification.isUsed(),
                verification.getCreatedAt(),
                verification.getExpiredAt()
        );

        return mongoTemplate.insert(entity).getId().toString();
    }

    // Search by id
    public Optional<VerificationDomain> getById(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }

        Query query = new Query(Criteria.where("id").is(new ObjectId(id)));
        return Optional.ofNullable(mongoTemplate.findOne(query, VerificationEntity.class))
                .map(this::mapping);
    }


    // Search by value
    public Optional<VerificationDomain> getByValue(String value) {
        Query query = new Query(Criteria.where("value").is(value));
        return Optional.ofNullable(mongoTemplate.findOne(query, VerificationEntity.class))
                .map(this::mapping);
    }

    public Optional<VerificationDomain> getByIdAndValue(ObjectId id, String value) {
        Query query = new Query(
                new Criteria().andOperator(
                        Criteria.where("value").is(value),
                        Criteria.where("associatedId").is(id)
                )
        );
        return Optional.ofNullable(mongoTemplate.findOne(query, VerificationEntity.class))
                .map(this::mapping);
    }

    // Update as used
    public Optional<VerificationDomain> updateStatusToUsed(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }

        Query query = new Query(Criteria.where("id").is(new ObjectId(id)));
        Update update = new Update().set("used", true);
        UpdateResult result = mongoTemplate.updateFirst(query, update, VerificationEntity.class);

        return result.getModifiedCount() > 0 ? getById(id) : Optional.empty();
    }

    public boolean updateStatusToUsed(String associatedId, String value) {
        if (!ObjectId.isValid(associatedId) || value == null || value.isEmpty()) {
            return false;
        }

        Query query = new Query(Criteria.where("associatedId").is(new ObjectId(associatedId))
                .and("value").is(value)
                .and("used").is(false)); // Ensure the token isn't already used

        Update update = new Update()
                .set("used", true)
                .set("updatedAt", Instant.now()); // Optional: Track update time

        UpdateResult result = mongoTemplate.updateFirst(query, update, VerificationEntity.class);

        // ✅ Return `true` if at least one document was modified
        return result.getModifiedCount() > 0;
    }



    // Delete by id
    public Optional<Boolean> deleteById(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }
        Query query = new Query(Criteria.where("id").is(new ObjectId(id)));
        DeleteResult result = mongoTemplate.remove(query, VerificationEntity.class);
        return Optional.of(result.getDeletedCount() > 0);
    }


    // Delete all used
    public int deleteAllUsed() {
        Query query = new Query(Criteria.where("used").is(true)); // Find expired entries
        DeleteResult result = mongoTemplate.remove(query, VerificationEntity.class);
        return (int) result.getDeletedCount();
    }


    private VerificationDomain mapping(VerificationEntity entity) {
        return new VerificationDomain(
                entity.getId().toString(),
                entity.getAssociatedId() != null ? entity.getAssociatedId().toString() : null,
                entity.getAssociatedUsername(),
                VerificationType.fromString(entity.getVerificationType()),
                entity.getValue(),
                entity.isUsed(),
                entity.getCreatedAt(),
                entity.getExpireAt()
        );
    }
}
