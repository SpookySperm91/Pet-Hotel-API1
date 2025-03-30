package john.api1.application.services.boarding;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.dto.mapper.BoardingCreatedDTO;
import john.api1.application.dto.request.BoardingRDTO;
import john.api1.application.ports.repositories.boarding.IBoardingCreateRepository;
import john.api1.application.ports.repositories.owner.IAccountSearchRepository;
import john.api1.application.ports.repositories.owner.IPetOwnerCQRSRepository;
import john.api1.application.ports.repositories.pet.IPetCQRSRepository;
import john.api1.application.ports.repositories.pet.IPetsSearchRepository;
import john.api1.application.ports.services.boarding.IBoardingCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BoardingAS implements IBoardingCreate {
    private final IAccountSearchRepository accountSearch;
    private final IPetsSearchRepository petsSearch;
    private final IBoardingCreateRepository createRepository;
    private final IPetCQRSRepository petCQRS;
    private final IPetOwnerCQRSRepository petOwnerCQRS;


    @Autowired
    public BoardingAS(IAccountSearchRepository accountSearch,
                      IPetsSearchRepository petsSearch,
                      IBoardingCreateRepository createRepository,
                      IPetCQRSRepository petCQRS,
                      @Qualifier("MongoAccountSearchRepo") IPetOwnerCQRSRepository petOwnerCQRS) {
        this.accountSearch = accountSearch;
        this.petsSearch = petsSearch;
        this.createRepository = createRepository;
        this.petCQRS = petCQRS;
        this.petOwnerCQRS = petOwnerCQRS;
    }

    // Check both owner and pet if exist
    // Check pet if currently boarding
    // Create boarding domain object
    // Save to DB
    // Return response
    public DomainResponse<BoardingCreatedDTO> createBoarding(BoardingRDTO boardingRequest, BoardingType boardingType, PaymentStatus paymentStatus) {
        try {
            if (!accountSearch.existById(boardingRequest.getOwnerId())) {
                return DomainResponse.error("Owner does not exist");
            }
            if (petsSearch.isPetBoarding(boardingRequest.getPetId())) {
                return DomainResponse.error("Pet is currently boarding");
            }

            BoardingDomain boarding = BoardingDomain.create(
                    boardingRequest.getPetId(),
                    boardingRequest.getOwnerId(),
                    boardingType,
                    boardingRequest.getBoardingStart(),
                    boardingRequest.getBoardingEnd(),
                    boardingRequest.getInitialPayment(),
                    paymentStatus,
                    boardingRequest.getNotes()
            );
            String boardingId  = createRepository.saveBoarding(boarding);

            // Aggregate to DTO
            var pet = petCQRS.getPetDetails(boardingRequest.getPetId());
            var owner = petOwnerCQRS.getDetails(boardingRequest.getOwnerId());

            if(pet.isEmpty() || owner.isEmpty()) {
                return DomainResponse.error("Failed to retrieve pet or owner details");
            }


            return pet.flatMap(p -> owner.map(o ->
                            new BoardingCreatedDTO(
                                    // id
                                    boardingId, boardingRequest.getPetId(), boardingRequest.getOwnerId(),
                                    // pet
                                    p.petName(), p.animalType(), p.breed(), p.size(), p.age(),
                                    // owner
                                    o.ownerName(), o.ownerEmail(), o.ownerPhoneNumber(),
                                    String.join(", ", o.streetAddress(), o.cityAddress(), o.stateAddress()),
                                    // boarding
                                    boardingType.getBoardingType(),
                                    boardingRequest.getBoardingStart(),
                                    boardingRequest.getBoardingEnd(),
                                    boardingRequest.getInitialPayment(),
                                    paymentStatus.getPaymentStatus(),
                                    boardingRequest.getNotes(),
                                    Instant.now()
                            )
                    )).map(dto -> DomainResponse.success(dto, "Pet successfully boarded"))
                    .orElseGet(() -> DomainResponse.error("Failed to retrieve pet or owner details"));

        } catch (DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("Database error occurred");
        }
    }
}
