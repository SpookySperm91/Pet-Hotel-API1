package john.api1.application.services.boarding;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.dto.request.BoardingRDTO;
import john.api1.application.ports.repositories.account.IAccountSearchRepository;
import john.api1.application.ports.repositories.pet.IPetsSearchRepository;
import john.api1.application.ports.services.boarding.IBoardingCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardingAS implements IBoardingCreate {
    private final IAccountSearchRepository accountSearch;
    private final IPetsSearchRepository petsSearch;


    @Autowired
    public BoardingAS(IAccountSearchRepository accountSearch, IPetsSearchRepository petsSearch) {
        this.accountSearch = accountSearch;
        this.petsSearch = petsSearch;
    }

    // Check both owner and pet if exist
    // Create boarding domain object
    // Save to DB
    // Return response
    public DomainResponse<String> createBoarding(BoardingRDTO boardingRequest, BoardingType boardingType, PaymentStatus paymentStatus) {
        if (!accountSearch.existById(boardingRequest.getOwnerId())
                || !petsSearch.existsById(boardingRequest.getPetId())) {
            return DomainResponse.error("Owner or Pet does not exist");
        }


        BoardingDomain domain = new BoardingDomain(
                boardingRequest.getPetId(),
                boardingRequest.getOwnerId(),
                boardingType,
                boardingRequest.getBoardingStart(),
                boardingRequest.getBoardingEnd(),
                boardingRequest.getInitialPayment(),
                paymentStatus,
                boardingRequest.getNotes()
        );


        return DomainResponse.success();
    }
}
