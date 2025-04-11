package john.api1.application.adapters.repositories.request;

import john.api1.application.adapters.repositories.RequestEntity;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.ports.repositories.request.IRequestUpdateRepository;
import john.api1.application.ports.repositories.request.RequestCQRS;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class RequestUpdateRepository implements IRequestUpdateRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestUpdateRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void updateRequestStatus(String id, RequestStatus status) {
        Query query = createQuery(id);
        Update update = createUpdate(status, null);

        performUpdate(query, update);
    }

    @Override
    public void updateRequestStatusAndActive(String id, RequestStatus status, boolean active) {
        validateId(id);

        Query query = createQuery(id);
        Update update = createUpdate(status, null).set("active", active);

        performUpdate(query, update);
    }

    @Override
    public Optional<RequestDomain> updateToReject(String id, RequestStatus status, String rejectedDescription) {
        validateId(id);

        Query query = createQuery(id);
        Update update = createUpdate(status, rejectedDescription);

        RequestEntity updatedEntity = mongoTemplate.findAndModify(query, update, RequestEntity.class);
        return Optional.ofNullable(updatedEntity).map(this::toDomain);
    }

    @Override
    public Optional<RequestCQRS> updateRequestStatusReturnId(String id, RequestStatus status) {
        validateId(id);

        Query query = createQuery(id);
        Update update = createUpdate(status, null);
        RequestEntity updatedEntity = mongoTemplate.findAndModify(query, update, RequestEntity.class);

        if (updatedEntity == null)
            throw new PersistenceException("No documents were updated. The request may not exist or has already been updated.");

        return Optional.of(updatedEntity)
                .map(this::toCQRS);
    }

    @Override
    public Optional<RequestCQRS> updateRequestStatusAndActiveReturnId(String id, RequestStatus status, boolean active) {
        validateId(id);

        Query query = createQuery(id);
        Update update = createUpdate(status, null).set("active", active);
        RequestEntity updatedEntity = mongoTemplate.findAndModify(query, update, RequestEntity.class);

        if (updatedEntity == null)
            throw new PersistenceException("No documents were updated. The request may not exist or has already been updated.");

        return Optional.of(updatedEntity)
                .map(this::toCQRS);
    }


    ////////////////////
    // Helper methods //
    private Query createQuery(String id) {
        return new Query(Criteria.where("_id").is(new ObjectId(id)));
    }

    private Update createUpdate(RequestStatus status, String rejectedDescription) {
        Update update = new Update()
                .set("requestStatus", status.getRequestStatus())
                .set("updatedAt", Instant.now());

        if (rejectedDescription != null) {
            update.set("rejectDescription", rejectedDescription);
        }

        return update;
    }

    private void performUpdate(Query query, Update update) {
        var updateResult = mongoTemplate.updateFirst(query, update, RequestEntity.class);
        if (updateResult.getModifiedCount() == 0) {
            throw new PersistenceException("No documents were updated. The request may not exist.");
        }
    }

    private void validateId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid id cannot be converted to ObjectId");
    }

    private RequestDomain toDomain(RequestEntity entity) {
        return new RequestDomain(
                entity.getId().toString(),
                entity.getPetId().toString(),
                entity.getOwnerId().toString(),
                entity.getBoardingId().toString(),
                RequestType.fromString(entity.getRequestType()),
                RequestStatus.fromString(entity.getRequestStatus()),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getRejectDescription(),
                entity.isActive()
        );
    }

    private RequestCQRS toCQRS(RequestEntity entity) {
        return RequestCQRS.mapIds(
                entity.getId().toString(),
                entity.getOwnerId().toString(),
                entity.getPetId().toString(),
                entity.getBoardingId().toString()
        );

    }
}
