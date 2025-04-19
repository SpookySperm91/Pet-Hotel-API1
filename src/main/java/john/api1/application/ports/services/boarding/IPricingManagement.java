package john.api1.application.ports.services.boarding;

import john.api1.application.components.DomainResponse;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.ports.repositories.boarding.PricingCQRS;

import java.util.List;
import java.util.Optional;

public interface IPricingManagement {
    DomainResponse<Void> updateRequestBreakdown(String boardingId, BoardingPricingDomain.RequestBreakdown breakdowns); // add new request breakdown

    DomainResponse<String> deactivatePricing(String boardingId);

    DomainResponse<String> activatePricing(String boardingId);

    // Unsafe
    void unwrappedUpdateRequestBreakdown(String boardingId, BoardingPricingDomain.RequestBreakdown breakdowns); // add new request breakdown

    void unwrappedUpdateRequestBreakdown(String boardingId, List<BoardingPricingDomain.RequestBreakdown> breakdowns);


    // Readonly
    DomainResponse<BoardingPricingDomain> getPricingDetails(String boardingId);

    List<BoardingPricingDomain.RequestBreakdown> getBreakdown(String boardingId);

    Optional<PricingCQRS> getBoardingPricingCqrs(String boardingId);

}
