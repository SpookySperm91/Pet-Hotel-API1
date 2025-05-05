package john.api1.application.domain.cores.boarding;

import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.domain.models.boarding.BoardingDomain;

import java.time.Duration;
import java.time.Instant;

public class BoardingManagementDS {
    public static void validateRelease(BoardingDomain boardingStatus) {
        if (boardingStatus.getBoardingStatus() == BoardingStatus.RELEASED)
            throw new DomainArgumentException("Boarding is already released");
        if (boardingStatus.getBoardingStatus() == BoardingStatus.BOARDING)
            throw new DomainArgumentException("Boarding is not done boarding");
        if (boardingStatus.getPaymentStatus() != PaymentStatus.PAID)
            throw new DomainArgumentException("Boarding is not paid yet");
    }

    public static long calculateBoardingDurationHours(BoardingDomain domain) {
        Instant start = domain.getBoardingStart();
        Instant end = domain.getBoardingEnd();
        if (start == null || end == null) {
            throw new DomainArgumentException("Start time or end time cannot be null");
        }
        return Duration.between(start, end).toHours();
    }

    public static long calculateBoardingDurationDays(BoardingDomain domain) {
        long hours = calculateBoardingDurationHours(domain);
        return hours / 24;
    }

    public static long calculateBoardingDurationHours(Instant start, Instant end) {
        if (start == null || end == null) {
            throw new DomainArgumentException("Start time or end time cannot be null");
        }
        return Duration.between(start, end).toHours();
    }

    public static long calculateBoardingDurationDays(Instant start, Instant end) {
        long hours = calculateBoardingDurationHours(start, end);
        return hours / 24;
    }

}
