package john.api1.application.adapters.repositories.request;

import john.api1.application.adapters.repositories.ExtensionEntity;
import john.api1.application.adapters.repositories.GroomingEntity;
import john.api1.application.components.enums.GroomingType;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;
import john.api1.application.ports.repositories.request.ExtensionCQRS;
import john.api1.application.ports.repositories.request.GroomingCQRS;
import john.api1.application.ports.repositories.request.IRequestCompletedSearchRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RequestCompletedSearchRepository implements IRequestCompletedSearchRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestCompletedSearchRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ExtensionDomain> getExtensionByCurrentBoarding(String boardingId) {
        if (!ObjectId.isValid(boardingId)) throw new PersistenceException("Invalid boarding Id");

        Query query = new Query(Criteria.where("boardingId").is(boardingId));
        List<ExtensionEntity> extensionEntities = mongoTemplate.find(query, ExtensionEntity.class);

        return extensionEntities.stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GroomingDomain> getGroomingByRequestId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid grooming id");

        Query query = new Query(Criteria.where("requestId").is(new ObjectId(id)));
        GroomingEntity groomingEntity = mongoTemplate.findOne(query, GroomingEntity.class);

        return Optional.ofNullable(groomingEntity).map(this::convertToDomain);
    }


    @Override
    public Optional<ExtensionDomain> getExtensionByRequestId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid extension id");

        Query query = new Query(Criteria.where("requestId").is(new ObjectId(id)));
        ExtensionEntity extensionEntity = mongoTemplate.findOne(query, ExtensionEntity.class);

        return Optional.ofNullable(extensionEntity).map(this::convertToDomain);
    }


    // CQRS
    @Override
    public Optional<GroomingCQRS> getGroomingByRequestIdCqrs(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid grooming id");

        Query query = new Query(Criteria.where("requestId").is(new ObjectId(id)));
        GroomingEntity groomingEntity = mongoTemplate.findOne(query, GroomingEntity.class);

        return Optional.ofNullable(groomingEntity).map(
                n -> new GroomingCQRS(
                        GroomingType.safeFromStringOrNull(n.getGroomingType()),
                        n.getGroomingPrice(),
                        n.getDescription(),
                        n.getCreatedAt(),
                        n.getUpdatedAt()
                )
        );
    }

    @Override
    public Optional<ExtensionCQRS> getExtensionByRequestIdCqrs(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid extension id");

        Query query = new Query(Criteria.where("requestId").is(new ObjectId(id)));
        ExtensionEntity extensionEntity = mongoTemplate.findOne(query, ExtensionEntity.class);

        return Optional.ofNullable(extensionEntity).map(
                n -> new ExtensionCQRS(
                        n.getExtendedHours(),
                        n.getAdditionalPrice(),
                        BoardingType.fromStringOrDefault(n.getDurationType())
                )
        );
    }

    private ExtensionDomain convertToDomain(ExtensionEntity entity) {
        return new ExtensionDomain(
                entity.getId().toString(),
                entity.getRequestId().toString(),
                entity.getBoardingId().toString(),
                entity.getAdditionalPrice(),
                entity.getExtendedHours(),
                BoardingType.fromStringOrDefault(entity.getDurationType()),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isApproved()
        );
    }

    private GroomingDomain convertToDomain(GroomingEntity entity) {
        return new GroomingDomain(
                entity.getId().toString(),
                entity.getRequestId().toString(),
                entity.getBoardingId().toString(),
                GroomingType.safeFromStringOrDefault(entity.getGroomingType()),
                entity.getGroomingPrice(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isApproved()
        );
    }

}
