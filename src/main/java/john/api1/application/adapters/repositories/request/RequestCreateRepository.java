package john.api1.application.adapters.repositories.request;

import john.api1.application.adapters.repositories.ExtensionEntity;
import john.api1.application.adapters.repositories.GroomingEntity;
import john.api1.application.adapters.repositories.RequestEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.ports.repositories.request.IRequestCreateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RequestCreateRepository implements IRequestCreateRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestCreateRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<String> createRequest(RequestDomain request) {
        validateId(request.getOwnerId(), request.getPetId(), request.getBoardingId());

        RequestEntity entity = RequestEntity.create(
                new ObjectId(request.getOwnerId()),
                new ObjectId(request.getPetId()),
                new ObjectId(request.getBoardingId()),
                request.getRequestType().getRequestType(),
                request.getRequestStatus().getRequestStatus(),
                request.getDescription(),
                request.getRequestTime());

        return Optional.ofNullable(mongoTemplate.save(entity))
                .map(RequestEntity::getId)
                .map(ObjectId::toString);
    }


    @Override
    public Optional<String> createInitialRequestExtension(ExtensionDomain request) {
        if (!ObjectId.isValid(request.getRequestId())) throw new PersistenceException("Invalid request-id");
        if (!ObjectId.isValid(request.getBoardingId())) throw new PersistenceException("Invalid boarding-id");

        ExtensionEntity entity = ExtensionEntity.create(
                new ObjectId(request.getRequestId()),
                new ObjectId(request.getBoardingId()),
                request.getAdditionalPrice(),
                request.getExtendedHours(),
                request.getDurationType().getDurationType(),
                request.getDescription(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.isApproved()
        );

        return Optional.ofNullable(mongoTemplate.save(entity))
                .map(ExtensionEntity::getId)
                .map(ObjectId::toString);
    }

    @Override
    public Optional<String> createInitialRequestGrooming(GroomingDomain request) {
        if (!ObjectId.isValid(request.getRequestId())) throw new PersistenceException("Invalid request-id");
        if (!ObjectId.isValid(request.getBoardingId())) throw new PersistenceException("Invalid boarding-id");

        GroomingEntity entity = GroomingEntity.create(
                new ObjectId(request.getRequestId()),
                new ObjectId(request.getBoardingId()),
                request.getGroomingType().getGroomingType(),
                request.getGroomingPrice(),
                request.getDescription(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.isApproved()
        );

        return Optional.ofNullable(mongoTemplate.save(entity))
                .map(GroomingEntity::getId)
                .map(ObjectId::toString);
    }


    private void validateId(String ownerId, String petId, String boardingId) {
        if (!ObjectId.isValid(ownerId)) throw new PersistenceException("Invalid owner-id");
        if (!ObjectId.isValid(petId)) throw new PersistenceException("Invalid pet-id");
        if (!ObjectId.isValid(boardingId)) throw new PersistenceException("Invalid boarding-id");
    }
}
