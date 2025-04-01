package john.api1.application.domain.cores.boarding;

import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;

import java.util.Collections;
import java.util.Optional;

// Pricing business logics
public class BoardingPricingDS {

    // Boarding total
    public static double getBoardingTotal(BoardingPricingDomain domain) {
        if (domain.getBoardingType() == BoardingType.LONG_STAY) {
            return domain.getRatePerHour() * 24 * domain.getBoardingDuration(); // Fixed 24-hour rate per day
        }
        return domain.getRatePerHour() * domain.getBoardingDuration(); // Per-hour charge for daycare
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


    // Final total (boarding? + request)
    public static double getFinalTotal(BoardingPricingDomain domain) {
        return domain.isPrepaid() ? getRequestTotal(domain) : getOverallTotal(domain);
    }
}
