package john.api1.application.adapters.repositories.request;

import john.api1.application.adapters.repositories.RequestEntity;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.ports.repositories.request.IRequestSearchRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RequestSearchRepository implements IRequestSearchRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestSearchRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<RequestDomain> findById(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid id cannot be converted to ObjectId");

        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        RequestEntity entity = mongoTemplate.findOne(query, RequestEntity.class);

        return Optional.ofNullable(entity)
                .map(this::toDomain);

    }

    @Override
    public List<RequestDomain> findByBoardingId(String boardingId) {
        if (!ObjectId.isValid(boardingId))
            throw new PersistenceException("Invalid boarding id cannot be converted to ObjectId");

        Query query = new Query(Criteria.where("boardingId").is(new ObjectId(boardingId)));
        List<RequestEntity> entities = mongoTemplate.find(query, RequestEntity.class);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<RequestDomain> findAllByStatus(RequestStatus status) {
        if (status == null) throw new PersistenceException("Status cannot be null");

        Query query = new Query(Criteria.where("requestStatus").is(status.getRequestStatus()));
        List<RequestEntity> entities = mongoTemplate.find(query, RequestEntity.class);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    // pet, inactive
    @Override
    public List<RequestDomain> findAll() {
        Query query = new Query();
        List<RequestEntity> entities = mongoTemplate.find(query, RequestEntity.class);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<RequestDomain> findAllActive() {
        Query query = new Query(Criteria.where("active").is(true));
        List<RequestEntity> entities = mongoTemplate.find(query, RequestEntity.class);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<RequestDomain> findAllInactive() {
        Query query = new Query(Criteria.where("active").is(false));
        List<RequestEntity> entities = mongoTemplate.find(query, RequestEntity.class);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    // pet history
    @Override
    public List<RequestDomain> findAllByPetId(String petId) {
        if (!ObjectId.isValid(petId)) throw new PersistenceException("Invalid pet id cannot be converted to ObjectId");

        Query query = new Query(Criteria.where("petId").is(new ObjectId(petId)));
        List<RequestEntity> entities = mongoTemplate.find(query, RequestEntity.class);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }


    private RequestDomain toDomain(RequestEntity entity) {
        return new RequestDomain(
                entity.getId().toString(),
                entity.getOwnerId().toString(),
                entity.getPetId().toString(),
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
}
