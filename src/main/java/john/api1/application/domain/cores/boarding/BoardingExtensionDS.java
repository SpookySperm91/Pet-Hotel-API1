package john.api1.application.domain.cores.boarding;

import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.boarding.ExtensionDomain;
import john.api1.application.domain.models.boarding.RequestDomain;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class BoardingExtensionDS {
    public static Instant calculateFinalBoardingEnd(Instant boardingEnd, List<ExtensionDomain> extensions) {
        long totalExtensionHours = extensions.stream()
                .mapToLong(ExtensionDomain::getExtendedHours)
                .sum();

        return boardingEnd.plus(Duration.ofHours(totalExtensionHours));
    }
}
