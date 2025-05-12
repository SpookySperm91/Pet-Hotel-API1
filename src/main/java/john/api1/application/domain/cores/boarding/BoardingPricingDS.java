package john.api1.application.domain.cores.boarding;

import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.ports.repositories.boarding.PricingCQRS;

import java.util.Collections;
import java.util.Optional;

// Pricing business logics
public class BoardingPricingDS {

    // Boarding total
    public static double getBoardingTotal(BoardingPricingDomain domain) {
        return domain.getRatePerHour() * domain.getBoardingDuration(); // Per-hour charge for daycare
    }

    public static double getBoardingTotal(BoardingPricingDomain domain, long hours) {
        return domain.getRatePerHour() * hours; // Per-hour charge for daycare
    }

    public static double getBoardingTotal(PricingCQRS pricing) {
        return pricing.rate() * pricing.duration(); // Per-hour charge for daycare
    }

    // Request total
    public static double getRequestTotal(BoardingPricingDomain domain) {
        return Optional.ofNullable(domain.getRequestBreakdown())
                .orElse(Collections.emptyList()) // Ensure it's never null
                .stream()
                .mapToDouble(BoardingPricingDomain.RequestBreakdown::total)
                .sum();
    }

    // Overall total (boarding + request)
    public static double getOverallTotal(BoardingPricingDomain domain) {
        return getBoardingTotal(domain) + getRequestTotal(domain);
    }

    public static double getOverallTotal(BoardingPricingDomain domain, long hours) {
        return getBoardingTotal(domain, hours) + getRequestTotal(domain);
    }


    // Final total (boarding? + request)
    public static double getFinalTotal(BoardingPricingDomain domain) {
        return domain.isPrepaid() ? getRequestTotal(domain) : getOverallTotal(domain);
    }
}
