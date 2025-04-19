package john.api1.application.ports.repositories.boarding;

import john.api1.application.components.enums.boarding.BoardingType;

public record PricingCQRS(
        BoardingType type,
        double rate,
        long duration
) {
}
