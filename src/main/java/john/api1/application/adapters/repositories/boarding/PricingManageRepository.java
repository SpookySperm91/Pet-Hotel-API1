package john.api1.application.adapters.repositories.boarding;

import com.mongodb.client.result.UpdateResult;
import john.api1.application.adapters.repositories.PricingEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.ports.repositories.boarding.IPricingManagementRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class PricingManageRepository implements IPricingManagementRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PricingManageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<String> save(BoardingPricingDomain pricing) {
        if (!ObjectId.isValid(pricing.getBoardingId()))
            throw new PersistenceException("Invalid boarding ID format. Cannot be set as ObjectId.");

        PricingEntity pricingEntity = PricingEntity.create(
                new ObjectId(pricing.getBoardingId()),
                pricing.getRatePerHour(),
                pricing.getBoardingType().getBoardingType(),
                pricing.getBoardingDuration(),
                pricing.isPrepaid(),
                pricing.getRequestBreakdown(),
                pricing.isActive(),
                pricing.getDeactivatedAt()
        );

        return Optional.ofNullable(mongoTemplate.save(pricingEntity).getId().toString());
    }

    @Override
    public boolean updateBreakDownList(String boardingId, List<BoardingPricingDomain.RequestBreakdown> requestBreakdown) {
        if (!ObjectId.isValid(boardingId))
            throw new PersistenceException("Invalid boarding ID format. Cannot be set as ObjectId.");

        if (requestBreakdown == null)
            throw new PersistenceException("Request breakdown list cannot be null.");


        // Fetch the existing PricingEntity using the boardingId
        PricingEntity existingPricingEntity = mongoTemplate.findOne(
                new Query(Criteria.where("boardingId")
                        .is(new ObjectId(boardingId))),  // Corrected to ObjectId
                PricingEntity.class
        );

        if (existingPricingEntity == null) {
            throw new PersistenceException("Pricing entity not found for boardingId: " + boardingId);
        }

        if (!existingPricingEntity.isActive()) {
            throw new PersistenceException("Cannot update an inactive pricing entity.");
        }

        existingPricingEntity.setRequestBreakdown(requestBreakdown);
        mongoTemplate.save(existingPricingEntity);

        return true;
    }

    public void deactivatePricing(String boardingId) {
        Query query = new Query(Criteria.where("boardingId").is(new ObjectId(boardingId))
                .and("isActive").is(true)); // Only match if it's active
        Update update = new Update().set("isActive", false).set("deactivatedAt", Instant.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, PricingEntity.class);

        if (result.getModifiedCount() == 0) {
            throw new PersistenceException("Pricing entity not found or already deactivated for boardingId: " + boardingId);
        }
    }

}
