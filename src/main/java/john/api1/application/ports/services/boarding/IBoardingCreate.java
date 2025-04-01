package john.api1.application.ports.services.boarding;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.dto.mapper.boarding.BoardingCreatedDTO;
import john.api1.application.dto.request.BoardingRDTO;

public interface IBoardingCreate {
    // return aggregated values(?)
    DomainResponse<BoardingCreatedDTO> createNewBoarding(BoardingRDTO boardingRequest, BoardingType boardingType, PaymentStatus paymentStatus);
}
