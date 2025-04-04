package john.api1.application.ports.repositories.boarding;

import john.api1.application.domain.models.boarding.BoardingPricingDomain;

import java.util.List;
import java.util.Optional;

public interface IPricingManagementRepository {
    Optional<String> save(BoardingPricingDomain pricing);

    boolean updateBreakDownList(String boardingId, List<BoardingPricingDomain.RequestBreakdown> requestBreakdown);

    void deactivatePricing(String boardingId);

}
