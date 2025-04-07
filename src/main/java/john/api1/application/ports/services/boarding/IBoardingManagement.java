package john.api1.application.ports.services.boarding;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.boarding.BoardingReleasedDTO;
import john.api1.application.dto.request.BoardingStatusRDTO;
import john.api1.application.dto.request.PaymentStatusDTO;

public interface IBoardingManagement {
    DomainResponse<BoardingReleasedDTO> releasedBoarding(String boardingId); // release after completed
    DomainResponse<BoardingReleasedDTO> forceReleasedBoarding(String boardingId); // release after completed

    DomainResponse<Void> updatePaidStatus(PaymentStatusDTO paymentStatus); // after paid, or new pay service
    DomainResponse<Void> updateBoardingStatus(BoardingStatusRDTO boardingStatus); //overdue, active, released?

}
