package john.api1.application.adapters.repositories.boarding;

import com.google.common.base.Optional;
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

import java.time.Instant;

@Repository
public class BoardingManageRepository implements IBoardingCreateRepository, IBoardingManagementRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public BoardingManageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public String saveBoarding(BoardingDomain boarding) {
        if (!ObjectId.isValid(boarding.getOwnerId()) || !ObjectId.isValid(boarding.getPetId()))
            throw new PersistenceException("Invalid Id type");

        BoardingEntity entity = BoardingEntity.createWithDomain(boarding);
        return mongoTemplate.save(entity).getId().toString();
    }

    public boolean updatePaidStatus(String boardingId) {
        if (!ObjectId.isValid(boardingId) || !ObjectId.isValid(boardingId))
            throw new PersistenceException("Invalid boarding-id type");

        return updateBoardingField(boardingId, "paymentStatus", PaymentStatus.PAID.getPaymentStatus());
    }

    @Override
    public boolean markAsRelease(String boardingId) {
        return updateBoardingStatus(boardingId, BoardingStatus.RELEASED);
    }

    @Override
    public boolean markAsOverdue(String boardingId) {
        return updateBoardingStatus(boardingId, BoardingStatus.OVERDUE);
    }

    @Override
    public boolean markAsActive(String boardingId) {
        return updateBoardingStatus(boardingId, BoardingStatus.BOARDING);
    }


    private boolean updateBoardingStatus(String boardingId, BoardingStatus status) {
        return updateBoardingField(boardingId, "boardingStatus", status.getBoardingStatus());
    }

    // Query
    private boolean updateBoardingField(String boardingId, String field, Object value) {
        if (!ObjectId.isValid(boardingId)) {
            throw new PersistenceException("Invalid boarding-id type");
        }

        Query query = new Query(Criteria.where("_id").is(new ObjectId(boardingId)));
        Update update = new Update()
                .set(field, value)
                .set("updatedAt", Instant.now());

        return mongoTemplate.updateFirst(query, update, BoardingEntity.class).wasAcknowledged();
    }


    @Override
    public void deleteById(String boardingId) {
        if (!ObjectId.isValid(boardingId)) {
            throw new PersistenceException("Invalid boarding-id type");
        }

        Query query = new Query(Criteria.where("_id").is(new ObjectId(boardingId)));
        mongoTemplate.remove(query, BoardingEntity.class);
    }


}
