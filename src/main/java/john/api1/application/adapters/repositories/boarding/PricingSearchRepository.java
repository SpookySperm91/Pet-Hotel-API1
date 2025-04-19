package john.api1.application.adapters.repositories.boarding;

import john.api1.application.adapters.repositories.PricingEntity;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.ports.repositories.boarding.IPricingSearchRepository;
import john.api1.application.ports.repositories.boarding.PricingCQRS;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PricingSearchRepository implements IPricingSearchRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PricingSearchRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<BoardingPricingDomain.RequestBreakdown> getRequestBreakdown(String boardingId) {
        if (!ObjectId.isValid(boardingId)) {
            throw new PersistenceException("Invalid boarding-id type");
        }

        Query query = new Query(Criteria.where("boardingId").is(new ObjectId(boardingId)));
        query.fields().include("requestBreakdown"); // Fetch only breakdown field

        PricingEntity pricingEntity = mongoTemplate.findOne(query, PricingEntity.class);

        if (pricingEntity == null || pricingEntity.getRequestBreakdown() == null) {
            throw new PersistenceException("Pricing entity not found or no request breakdown for boardingId: " + boardingId);
        }

        return pricingEntity.getRequestBreakdown();
    }

    @Override
    public Optional<BoardingPricingDomain> getBoardingPricing(String boardingId) {
        if (!ObjectId.isValid(boardingId)) {
            throw new PersistenceException("Invalid boarding-id type");
        }

        Query query = new Query(Criteria.where("boardingId").is(new ObjectId(boardingId)));
        PricingEntity pricingEntity = mongoTemplate.findOne(query, PricingEntity.class);

        if (pricingEntity == null) {
            return Optional.empty(); // Return empty if no pricing found
        }

        // Convert PricingEntity to BoardingPricingDomain
        BoardingPricingDomain pricingDomain = new BoardingPricingDomain(
                pricingEntity.getId().toString(),
                pricingEntity.getBoardingId().toString(),
                pricingEntity.getRatePerHour(),
                BoardingType.fromStringOrDefault(pricingEntity.getBoardingType()),
                pricingEntity.getBoardingDuration(),
                pricingEntity.isPrepaid(),
                pricingEntity.getRequestBreakdown(),
                pricingEntity.isActive(),
                pricingEntity.getDeactivatedAt()
        );

        return Optional.of(pricingDomain);
    }

    @Override
    public Optional<PricingCQRS> getBoardingPricingCqrs(String boardingId) {
        if (!ObjectId.isValid(boardingId)) {
            throw new PersistenceException("Invalid boarding-id type");
        }

        Query query = new Query(Criteria.where("boardingId").is(new ObjectId(boardingId)));
        PricingEntity pricingEntity = mongoTemplate.findOne(query, PricingEntity.class);

        if (pricingEntity == null) {
            return Optional.empty(); // Return empty if no pricing found
        }

        PricingCQRS pricing = new PricingCQRS(
                BoardingType.fromStringOrDefault(pricingEntity.getBoardingType()),
                pricingEntity.getRatePerHour(),
                pricingEntity.getBoardingDuration()
        );
        return Optional.of(pricing);
    }


}
