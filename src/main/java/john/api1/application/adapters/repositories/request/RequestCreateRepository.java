package john.api1.application.adapters.repositories.request;

import john.api1.application.adapters.repositories.RequestEntity;
import john.api1.application.components.exception.PersistenceException;
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

    private void validateId(String ownerId, String petId, String boardingId) {
        if (!ObjectId.isValid(ownerId)) throw new PersistenceException("Invalid owner-id");
        if (!ObjectId.isValid(petId)) throw new PersistenceException("Invalid pet-id");
        if (!ObjectId.isValid(boardingId)) throw new PersistenceException("Invalid boarding-id");
    }
}
