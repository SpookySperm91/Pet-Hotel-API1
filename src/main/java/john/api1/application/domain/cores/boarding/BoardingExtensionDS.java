package john.api1.application.domain.cores.boarding;

import john.api1.application.domain.models.request.ExtensionDomain;

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
