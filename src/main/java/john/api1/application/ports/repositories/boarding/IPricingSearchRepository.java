package john.api1.application.ports.repositories.boarding;

import john.api1.application.domain.models.boarding.BoardingPricingDomain;

import java.util.List;
import java.util.Optional;

public interface IPricingSearchRepository {
    List<BoardingPricingDomain.RequestBreakdown> getRequestBreakdown(String boardingId);
    Optional<BoardingPricingDomain> getBoardingPricing(String boardingId);
}
