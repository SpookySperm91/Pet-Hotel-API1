package john.api1.application.adapters.repositories.boarding;

import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.ports.repositories.boarding.IPricingManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PricingManageRepository implements IPricingManagement {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PricingManageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate=mongoTemplate;
    }

    @Override
    public Optional<String> save(BoardingPricingDomain pricing) {
        return Optional.empty();
    }

    @Override
    public boolean updateList(List<BoardingPricingDomain.RequestBreakdown> requestBreakdown){
        return true;
    }




}
