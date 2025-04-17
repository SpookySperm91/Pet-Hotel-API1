package john.api1.application.services.boarding;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.ports.repositories.boarding.IPricingManagementRepository;
import john.api1.application.ports.repositories.boarding.IPricingSearchRepository;
import john.api1.application.ports.services.boarding.IPricingManagement;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = {DomainArgumentException.class, PersistenceException.class, MongoException.class})
public class PricingManagementAS implements IPricingManagement {
    private final IPricingManagementRepository pricingManagement;
    private final IPricingSearchRepository pricingSearch;

    @Autowired
    public PricingManagementAS(IPricingManagementRepository pricingManagement,
                               IPricingSearchRepository pricingSearch) {
        this.pricingManagement = pricingManagement;
        this.pricingSearch = pricingSearch;
    }

    public DomainResponse<Void> updateRequestBreakdown(String boardingId, BoardingPricingDomain.RequestBreakdown breakdowns) {
        try {
            if (!ObjectId.isValid(boardingId))
                throw new PersistenceException("Invalid boarding ID format. It cannot be an ObjectId.");

            var boarding = pricingSearch.getRequestBreakdown(boardingId);
            if (boarding.isEmpty()) return DomainResponse.error("Boarding price breakdown cannot be found");

            // add new list to existing
            boarding.add(breakdowns);

            boolean success = pricingManagement.updateBreakDownList(boardingId, boarding);
            if (success) return DomainResponse.success("Successfully update price breakdown");
            else return DomainResponse.error("Failed to update price breakdown");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("An unexpected error occurred: " + e.getMessage());
        }
    }

    public DomainResponse<String> deactivatePricing(String boardingId) {
        try {
            if (!ObjectId.isValid(boardingId))
                throw new PersistenceException("Invalid boarding ID format. It cannot be an ObjectId.");
            pricingManagement.deactivatePricing(boardingId);
        } catch (PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
        return DomainResponse.success();
    }

    // no uses
    public DomainResponse<String> activatePricing(String boardingId) {
        return DomainResponse.success();
    }


    // Unwrapped
    public void unwrappedUpdateRequestBreakdown(String boardingId, BoardingPricingDomain.RequestBreakdown breakdowns) {
        if (!ObjectId.isValid(boardingId))
            throw new PersistenceException("Invalid boarding ID format. It cannot be an ObjectId.");

        var breakdownListOpt = pricingSearch.getRequestBreakdown(boardingId);
        if (breakdownListOpt.isEmpty()) throw new PersistenceException("Boarding price breakdown cannot be found");

        // add new list to existing
        breakdownListOpt.add(breakdowns);
        boolean success = pricingManagement.updateBreakDownList(boardingId, breakdownListOpt);
        if (!success) throw new PersistenceException("Failed to update price breakdown");
    }

    public void unwrappedUpdateRequestBreakdown(String boardingId, List<BoardingPricingDomain.RequestBreakdown> breakdowns) {
        if (!ObjectId.isValid(boardingId))
            throw new PersistenceException("Invalid boarding ID format. It cannot be an ObjectId.");

        boolean success = pricingManagement.updateBreakDownList(boardingId, breakdowns);
        if (!success) throw new PersistenceException("Failed to update price breakdown");
    }


    // Readonly

    public DomainResponse<BoardingPricingDomain> getPricingDetails(String boardingId) {
        try {
            if (!ObjectId.isValid(boardingId))
                throw new PersistenceException("Invalid boarding ID format. It cannot be an ObjectId.");

            var pricingDetails = pricingSearch.getBoardingPricing(boardingId);
            return pricingDetails.map(DomainResponse::success)
                    .orElseGet(() -> DomainResponse.error("Failed to get pricing for boarding id: " + boardingId));

        } catch (PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    public List<BoardingPricingDomain.RequestBreakdown> getBreakdown(String boardingId) {
        if (!ObjectId.isValid(boardingId))
            throw new PersistenceException("Invalid boarding ID format. It cannot be an ObjectId.");

        var breakdownListOpt = pricingSearch.getRequestBreakdown(boardingId);
        if (breakdownListOpt.isEmpty()) return new ArrayList<>();

        return breakdownListOpt;
    }
}
