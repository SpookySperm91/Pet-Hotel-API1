package john.api1.application.services.boarding;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.boarding.BoardingExtensionDS;
import john.api1.application.domain.cores.boarding.BoardingManagementDS;
import john.api1.application.domain.cores.boarding.BoardingPricingDS;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.dto.mapper.boarding.BoardingReleasedDTO;
import john.api1.application.dto.mapper.boarding.RequestBreakdownDTO;
import john.api1.application.dto.request.BoardingStatusDTO;
import john.api1.application.dto.request.PaymentStatusDTO;
import john.api1.application.ports.repositories.boarding.IBoardingManagementRepository;
import john.api1.application.ports.repositories.boarding.IBoardingSearchRepository;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.repositories.request.IRequestSearchRepository;
import john.api1.application.ports.services.IPetOwnerManagement;
import john.api1.application.ports.services.boarding.IBoardingManagement;
import john.api1.application.ports.services.boarding.IPricingManagement;
import john.api1.application.ports.services.pet.IPetUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

// THIS APPLICATION SERVICE IS FOR UPDATING CURRENT BOARDING
@Service
public class BoardingManagementAS implements IBoardingManagement {
    private final IBoardingSearchRepository boardingSearch;
    private final IBoardingManagementRepository boardingManagement;
    private final IRequestSearchRepository requestManagement;
    private final IPricingManagement pricingManagement;
    private final IPetUpdate petUpdate;
    private final IPetOwnerManagement ownerSearch;


    @Autowired
    public BoardingManagementAS(// repositories
                                IBoardingSearchRepository boardingSearch,
                                IBoardingManagementRepository boardingManagement,
                                IRequestSearchRepository requestManagement,
                                // services
                                IPricingManagement pricingManagement,
                                IPetUpdate petUpdate,
                                IPetOwnerManagement ownerSearch) {
        this.boardingSearch = boardingSearch;
        this.boardingManagement = boardingManagement;
        this.requestManagement = requestManagement;
        this.pricingManagement = pricingManagement;
        this.petUpdate = petUpdate;
        this.ownerSearch = ownerSearch;
    }

    // Validate current boarding status
    // Update boarding status before release
    // Update boarding DB
    // Retrieve boarding details for aggregation return data
    @Override
    public DomainResponse<BoardingReleasedDTO> releasedBoarding(String boardingId) {
        var boarding = boardingSearch.searchById(boardingId);
        if (boarding.isEmpty()) return DomainResponse.error("Boarding do not exist");

        try {
            // Check current boarding status
            BoardingManagementDS.validateRelease(boarding.get());
            return release(boarding.get(), boardingId);
        } catch (DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    @Override
    public DomainResponse<BoardingReleasedDTO> forceReleasedBoarding(String boardingId) {
        var boarding = boardingSearch.searchById(boardingId);
        if (boarding.isEmpty()) return DomainResponse.error("Boarding do not exist");

        // Cannot release closed boarding lmao
        if (boarding.get().getBoardingStatus() == BoardingStatus.RELEASED) {
            return DomainResponse.error("Boarding is already released");
        }
        return release(boarding.get(), boardingId);
    }


    private DomainResponse<BoardingReleasedDTO> release(BoardingDomain boarding, String boardingId) {
        try {
            // release, deactivate boarding and pricing
            boarding.updateBoardingStatus(BoardingStatus.RELEASED);  // RELEASED = auto set active as false
            boarding.updatePaymentStatus(PaymentStatus.PAID);

            boardingManagement.updateBoardingAfterRelease(boarding);
            pricingManagement.deactivatePricing(boardingId);


            // update pet current status
            var petUpdated = petUpdate.updatePetStatusWithResponse(boarding.getPetId(), BoardingStatus.RELEASED);
            if (!petUpdated.isSuccess()) {
                return DomainResponse.error(petUpdated.getMessage());
            }

            // Fetch related data (optimize batch-fetch if possible)
            var ownerDetail = ownerSearch.getPetOwnerBoardingDetails(boarding.getOwnerId());
            var boardingPrice = pricingManagement.getPricingDetails(boarding.getId());
            var extensions = requestManagement.getExtensionByCurrentBoarding(boardingId);
            Instant extendedTotalTime = BoardingExtensionDS.calculateFinalBoardingEnd(boarding.getBoardingEnd(), extensions);

            // Return response
            Instant now = Instant.now();
            var dto = mapping(boarding, petUpdated.getData(), ownerDetail, boardingPrice.getData(), extendedTotalTime, now);
            System.out.println();
            String message = String.format(
                    "%s's pet '%s' is successfully released from boarding at %s"
                    , ownerDetail.ownerName(), petUpdated.getData().petName(), now);
            return DomainResponse.success(dto, message);
        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    // Update payment status
    @Override
    public DomainResponse<Void> updatePaidStatus(PaymentStatusDTO paymentStatus) {
        try {
            var status = PaymentStatus.safeFromStringOrDefault(paymentStatus.getStatus());
            boardingManagement.updatePaidStatus(paymentStatus.getId(), status);

            return DomainResponse.success("Successfully update payment status to '" + status.getPaymentStatus() + "'");
        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("There was an issue with the database. Please try again.");
        }
    }

    // Update boarding status
    @Override
    public DomainResponse<Void> updateBoardingStatus(BoardingStatusDTO boardingStatus) {
        try {
            var status = BoardingStatus.safeFromStringOrDefault(boardingStatus.getStatus());
            switch (status) {
                case BOARDING -> boardingManagement.markAsActive(boardingStatus.getId());
                case DONE_BOARDING -> boardingManagement.markAsDoneBoarding(boardingStatus.getId());
                case OVERDUE -> boardingManagement.markAsOverdue(boardingStatus.getId());
                case RELEASED -> boardingManagement.markAsRelease(boardingStatus.getId());
            }

            return DomainResponse.success("Successfully update boarding status to '" + status.getBoardingStatus() + "'");
        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("There was an issue with the database. Please try again.");
        }
    }

    private BoardingReleasedDTO mapping(BoardingDomain boarding,
                                        PetCQRS pet,
                                        PetOwnerCQRS petOwner,
                                        BoardingPricingDomain pricing,
                                        Instant extensionTime,
                                        Instant releasedAt) {
        return new BoardingReleasedDTO(
                // id
                boarding.getId(), boarding.getPetId(), boarding.getOwnerId(),
                // pet
                pet.petName(), pet.animalType(), pet.breed(), pet.size(), pet.age(),
                // owner
                petOwner.ownerName(), petOwner.ownerEmail(), petOwner.ownerPhoneNumber(),
                String.join(", ", petOwner.streetAddress(), petOwner.cityAddress(), petOwner.stateAddress()),
                // boarding details
                boarding.getBoardingStatus().getBoardingStatus(), boarding.getBoardingType().getBoardingType(),
                boarding.getBoardingStart(), boarding.getBoardingEnd(), extensionTime, releasedAt, boarding.getNotes(),
                // pricing
                boarding.getPaymentStatus().getPaymentStatus(),
                BoardingPricingDS.getBoardingTotal(pricing),
                RequestBreakdownDTO.map(pricing.getRequestBreakdown()),
                BoardingPricingDS.getFinalTotal(pricing),
                boarding.getCreatedAt()
        );
    }

}
