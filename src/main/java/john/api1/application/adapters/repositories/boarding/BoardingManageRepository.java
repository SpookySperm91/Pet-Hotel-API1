package john.api1.application.adapters.repositories.boarding;

import john.api1.application.adapters.repositories.BoardingEntity;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.ports.repositories.boarding.IBoardingCreateRepository;
import john.api1.application.ports.repositories.boarding.IBoardingManagementRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
public class BoardingManageRepository implements IBoardingCreateRepository, IBoardingManagementRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public BoardingManageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public String saveBoarding(BoardingDomain boarding) {
        validateObjectId(boarding.getOwnerId(), "ownerId");
        validateObjectId(boarding.getPetId(), "petId");

        BoardingEntity entity = BoardingEntity.createWithDomain(boarding);
        return mongoTemplate.save(entity).getId().toString();
    }

    public void updateBoarding(BoardingDomain domain) {
        if (!ObjectId.isValid(domain.getId()))
            throw new PersistenceException("Invalid boarding id cannot convert to ObjectId");

        ObjectId objectId = new ObjectId(domain.getId());
        Query query = new Query(Criteria.where("_id").is(objectId));
        boolean exists = mongoTemplate.exists(query, BoardingEntity.class);

        if (!exists) throw new PersistenceException("Boarding with id " + domain.getId() + " does not exist");

        BoardingEntity entity = BoardingEntity.map(domain);
        mongoTemplate.save(entity);
    }


    // Update after release. Assume domain object fields are already updated.
    public void updateBoardingAfterRelease(BoardingDomain boarding) {
        if (boarding == null) {
            throw new PersistenceException("Boarding data cannot be null");
        }

        validateObjectId(boarding.getId(), "boardingId");
        validateObjectId(boarding.getPetId(), "petId");
        validateObjectId(boarding.getOwnerId(), "ownerId");

        Query query = new Query(Criteria.where("_id").is(new ObjectId(boarding.getId())));
        Update update = new Update()
                .set("boardingStatus", boarding.getBoardingStatus().getBoardingStatus())
                .set("paymentStatus", boarding.getPaymentStatus().getPaymentStatus())
                .set("updatedAt", Instant.now())
                .set("active", false);

        mongoTemplate.updateFirst(query, update, BoardingEntity.class);
    }

    public void updatePaidStatus(String boardingId, PaymentStatus status) {
        validateObjectId(boardingId, "boardingId");

        updateBoardingField(boardingId, "paymentStatus", status.getPaymentStatus());
    }

    @Override
    public void markAsRelease(String boardingId) {
        updateBoardingStatus(boardingId, BoardingStatus.RELEASED);
    }

    @Override
    public void markAsDoneBoarding(String boardingId) {
        updateBoardingStatus(boardingId, BoardingStatus.DONE_BOARDING);
    }

    @Override
    public void markAsOverdue(String boardingId) {
        updateBoardingStatus(boardingId, BoardingStatus.OVERDUE);
    }

    @Override
    public void markAsActive(String boardingId) {
        updateBoardingStatus(boardingId, BoardingStatus.BOARDING);
    }


    private void updateBoardingStatus(String boardingId, BoardingStatus status) {
        updateBoardingField(boardingId, "boardingStatus", status.getBoardingStatus());
    }

    // Query
    private void updateBoardingField(String boardingId, String field, Object value) {
        validateObjectId(boardingId, "boardingId");

        Query query = new Query(Criteria.where("_id").is(new ObjectId(boardingId)));
        Update update = new Update()
                .set(field, value)
                .set("updatedAt", Instant.now());

        mongoTemplate.updateFirst(query, update, BoardingEntity.class);
    }

    @Override
    public void deleteById(String boardingId) {
        validateObjectId(boardingId, "boardingId");

        Query query = new Query(Criteria.where("_id").is(new ObjectId(boardingId)));
        BoardingEntity boardingEntity = mongoTemplate.findOne(query, BoardingEntity.class);
        if (boardingEntity == null) {
            throw new PersistenceException("Boarding entity with ID " + boardingId + " not found");
        }

        mongoTemplate.remove(query, BoardingEntity.class);
    }

    private void validateObjectId(String id, String fieldName) {
        if (id == null || !ObjectId.isValid(id)) {
            throw new PersistenceException("Invalid " + fieldName + " ID type");
        }
    }

}
