package john.api1.application.domain.cores.boarding;

import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.domain.models.boarding.BoardingDomain;

public class BoardingManagementDS {
    public static void validateRelease(BoardingDomain boardingStatus) {
        if (boardingStatus.getBoardingStatus() == BoardingStatus.RELEASED) throw new DomainArgumentException("Boarding is already released");
        if (boardingStatus.getBoardingStatus() == BoardingStatus.BOARDING) throw new DomainArgumentException("Boarding is not done boarding");
        if (boardingStatus.getPaymentStatus() != PaymentStatus.PAID) throw new DomainArgumentException("Boarding is not paid yet");
    }
}
