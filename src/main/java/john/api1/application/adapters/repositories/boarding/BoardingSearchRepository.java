package john.api1.application.adapters.repositories.boarding;

import john.api1.application.adapters.repositories.BoardingEntity;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.ports.repositories.boarding.IBoardingSearchRepository;
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
public class BoardingSearchRepository implements IBoardingSearchRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public BoardingSearchRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<BoardingDomain> searchById(String id) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid boarding id cannot be converted to ObjectId.");

        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        BoardingEntity entity = mongoTemplate.findOne(query, BoardingEntity.class);

        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<BoardingDomain> searchAllByOwnerId(String ownerId) {
        if (!ObjectId.isValid(ownerId)) {
            return List.of();
        }

        Query query = new Query(Criteria.where("ownerId").is(new ObjectId(ownerId)));
        return fetchBoardings(query);
    }

    @Override
    public List<BoardingDomain> searchAllByPetId(String petId) {
        if (!ObjectId.isValid(petId)) {
            return List.of();
        }

        Query query = new Query(Criteria.where("petId").is(new ObjectId(petId)));
        return fetchBoardings(query);
    }

    @Override
    public List<BoardingDomain> searchActiveBoardingsByOwnerId(String ownerId) {
        if (!ObjectId.isValid(ownerId)) {
            return List.of();
        }

        Query query = new Query(
                Criteria.where("ownerId").is(new ObjectId(ownerId))
                        .and("boardingStatus").is(BoardingStatus.BOARDING.getBoardingStatus())
        );
        return fetchBoardings(query);
    }

    @Override
    public List<BoardingDomain> searchAll() {
        return fetchBoardings(new Query());
    }

    @Override
    public List<BoardingDomain> searchByStatus(BoardingStatus status) {
        Query query = new Query(Criteria.where("boardingStatus").is(status.getBoardingStatus()));
        return fetchBoardings(query);
    }

    @Override
    public Optional<BoardingStatus> checkBoardingCurrentStatus(String id) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid boarding id cannot be converted to ObjectId.");

        Query query = new Query(Criteria.where("_id").is(id));
        query.fields().include("boardingStatus");

        var entity = mongoTemplate.findOne(query, BoardingEntity.class);
        if (entity == null || entity.getBoardingStatus() == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(BoardingStatus.safeFromStringOrDefault(entity.getBoardingStatus()));
    }


    private List<BoardingDomain> fetchBoardings(Query query) {
        return mongoTemplate.find(query, BoardingEntity.class)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private BoardingDomain toDomain(BoardingEntity entity) {
        return new BoardingDomain(
                entity.getId().toString(),
                entity.getPetId().toString(),
                entity.getOwnerId().toString(),
                BoardingType.fromStringOrDefault(entity.getBoardingCategory()),
                entity.getBoardingStart(),
                entity.getBoardingEnd(),
                BoardingStatus.fromStringOrDefault(entity.getBoardingStatus()),
                PaymentStatus.fromStringOrDefault(entity.getPaymentStatus()),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive()
        );
    }
}
