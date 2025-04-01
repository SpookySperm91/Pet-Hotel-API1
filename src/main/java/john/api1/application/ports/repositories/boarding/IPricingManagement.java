package john.api1.application.ports.repositories.boarding;

import john.api1.application.domain.models.boarding.BoardingPricingDomain;

import java.util.List;
import java.util.Optional;

public interface IPricingManagement {
    Optional<String> save(BoardingPricingDomain pricing);
    boolean updateList(List<BoardingPricingDomain.RequestBreakdown> requestBreakdown);


}
