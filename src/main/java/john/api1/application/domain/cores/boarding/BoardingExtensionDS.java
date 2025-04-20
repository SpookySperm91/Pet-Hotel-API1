package john.api1.application.domain.cores.boarding;

import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.ports.repositories.request.ExtensionCQRS;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class BoardingExtensionDS {
    private static final int HOURS_PER_DAY = 24;

    public static Instant calculateFinalBoardingEnd(Instant boardingEnd, List<ExtensionDomain> extensions) {
        long totalExtensionHours = extensions.stream()
                .mapToLong(ExtensionDomain::getExtendedHours)
                .sum();

        return boardingEnd.plus(Duration.ofHours(totalExtensionHours));
    }

    public static Instant calculateFinalBoardingEnd(Instant boardingEnd, ExtensionCQRS extensions) {
        if (extensions == null) return boardingEnd;
        return boardingEnd.plus(Duration.ofHours(extensions.extendedHours()));
    }



    public static BoardingType extensionType(String extensionType) {
        return switch (extensionType) {
            case "HOURS" -> BoardingType.DAYCARE;
            case "DAYS" -> BoardingType.LONG_STAY;
            default ->
                    throw new DomainArgumentException("Extension type '" + extensionType + "' is invalid. Use 'HOURS' or 'DAYS'.");
        };
    }

    public static long calculateExtendedHours(BoardingType extensionType, BigDecimal hours) {
        if (extensionType.equals(BoardingType.LONG_STAY)) {
            return hours.multiply(BigDecimal.valueOf(24)).longValue();
        }
        return hours.longValue();
    }

    public static long determineDuration(ExtensionCQRS extension) {
        if (extension == null || extension.durationType() == null) return 0;

        if (extension.durationType() == BoardingType.LONG_STAY)
            return (long) Math.ceil(extension.extendedHours() / (double) HOURS_PER_DAY);

        return extension.extendedHours();
    }
}
