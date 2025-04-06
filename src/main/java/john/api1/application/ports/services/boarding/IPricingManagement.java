package john.api1.application.ports.services.boarding;

import john.api1.application.components.DomainResponse;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;

import java.util.List;

public interface IPricingManagement {
    DomainResponse<String> updateRequestBreakdown(String boardingId, List<BoardingPricingDomain.RequestBreakdown> breakdowns); // add new request breakdown
    DomainResponse<String> deactivatePricing(String boardingId);
    DomainResponse<String> activatePricing(String boardingId);
    DomainResponse<BoardingPricingDomain> getPricingDetails(String boardingId);

    // direct
}
