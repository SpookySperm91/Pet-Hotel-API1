package john.api1.application.ports.repositories.boarding;

import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.domain.models.boarding.BoardingDomain;

public interface IBoardingManagementRepository {
    void updateBoarding(BoardingDomain domain);
    void updateBoardingAfterRelease(BoardingDomain boarding);
    void updatePaidStatus(String boardingId, PaymentStatus status);

    void markAsRelease(String boardingId);
    void markAsDoneBoarding(String boardingId);

    void markAsOverdue(String boardingId);

    void markAsActive(String boardingId);

    void deleteById(String boardingId);
}
