package john.api1.application.services.request.commit;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.dto.mapper.request.commit.RequestCompletedServiceDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteServiceRDTO;
import john.api1.application.ports.repositories.boarding.IBoardingManagementRepository;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import john.api1.application.ports.services.boarding.IPricingManagement;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestSearch;
import john.api1.application.ports.services.request.IRequestUpdate;
import john.api1.application.ports.services.request.admin.ICommitRequestServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = {DomainArgumentException.class, PersistenceException.class, MongoException.class})
public class CommitRequestServicesAS implements ICommitRequestServices {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CommitRequestServicesAS.class);
    private final IBoardingManagementRepository boardingUpdate;
    private final IPricingManagement pricingSearch;
    private final IRequestUpdate requestUpdate;
    private final IBoardingSearch boardingSearch;
    private final IRequestSearch requestSearch;
    private final IPetSearch petSearch;

    @Autowired
    public CommitRequestServicesAS(IBoardingSearch boardingSearch,
                                   IBoardingManagementRepository boardingUpdate,
                                   IPricingManagement pricingSearch,
                                   IRequestSearch requestSearch,
                                   IRequestUpdate requestUpdate,
                                   IPetSearch petSearch) {
        this.boardingSearch = boardingSearch;
        this.boardingUpdate = boardingUpdate;
        this.pricingSearch = pricingSearch;
        this.requestSearch = requestSearch;
        this.requestUpdate = requestUpdate;
        this.petSearch = petSearch;
    }


    // Check request if valid and active
    // Check if pending request already have ready to active
    // Update request status as completed
    // Update boarding and payment status to pending
    // Update boarding pricing breakdown
    // update DB
    // Return aggregated DTO response
    @Override
    public DomainResponse<RequestCompletedServiceDTO> commitExtensionRequest(RequestCompleteServiceRDTO request) {
        // Rollback variables
        BoardingDomain originalBoarding = null;
        List<BoardingPricingDomain.RequestBreakdown> originalPricing = null;

        try {
            validateId(request.getRequestId());

            var check = requestSearch.searchByRequestId(request.getRequestId());
            var boarding = validateActiveRequest(check.getBoardingId());
            var extension = requestSearch.searchExtensionById(request.getServiceId());
            var pricing = pricingSearch.getBreakdown(check.getBoardingId());

            // Instantiates rollback variables
            originalBoarding = boarding.copy();
            originalPricing = new ArrayList<>(pricing);

            // Approve extension
            // Update boarding
            extension.markAsApproved();
            boarding.extendBoarding(extension.getExtendedHours());
            boarding.daycareToLongDay();
            boarding.updatePaymentStatus(PaymentStatus.PENDING);
            boarding.updateBoardingStatus(BoardingStatus.BOARDING);

            // Boarding pricing update
            var breakDown = BoardingPricingDomain.RequestBreakdown.createNew(
                    check.getId(),
                    check.getRequestType().getRequestType(),
                    extension.getAdditionalPrice(),
                    Instant.now()
            );
            pricing.add(breakDown);

            // Save all to DB
            requestUpdate.markRequestAsCompleted(check.getId());
            boardingUpdate.updateBoarding(boarding);
            pricingSearch.unwrappedUpdateRequestBreakdown(boarding.getId(), pricing);

            // DTO
            var dto = new RequestCompletedServiceDTO(
                    check.getId(),
                    check.getRequestType().getRequestType(),
                    RequestStatus.COMPLETED.getRequestStatus(),
                    Instant.now()
            );
            String petName = petSearch.getPetName(boarding.getPetId());
            String message = "The boarding extension for " + petName + " is completed.";

            return DomainResponse.success(dto, message);

        } catch (DomainArgumentException | PersistenceException e) {
            // Rollback
            log.warn("Initiating rollback for requestId: {}", request.getRequestId());
            try {
                if (originalBoarding != null && originalPricing != null) {
                    requestUpdate.rollbackAsActive(request.getRequestId());
                    boardingUpdate.updateBoarding(originalBoarding);
                    pricingSearch.unwrappedUpdateRequestBreakdown(originalBoarding.getId(), originalPricing);
                }
            } catch (Exception f) {
                log.error("Failed to delete photo request entry for requestId: {}", request.getRequestId(), f);
            }

            return DomainResponse.error(e.getMessage());
        }
    }

    // Check request if valid and active
    // Check if pending request already have ready to active
    // Update request status as completed
    // Update payment status to pending
    // Update boarding pricing breakdown
    // update DB
    // Return aggregated DTO response
    @Override
    public DomainResponse<RequestCompletedServiceDTO> commitGroomingRequest(RequestCompleteServiceRDTO request) {
        BoardingDomain originalBoarding = null;
        List<BoardingPricingDomain.RequestBreakdown> originalPricing = null;
        try {
            validateId(request.getRequestId());

            var check = requestSearch.searchByRequestId(request.getRequestId());
            var boarding = validateActiveRequest(check.getBoardingId());
            var grooming = requestSearch.searchGroomingById(request.getServiceId());
            var pricing = pricingSearch.getBreakdown(check.getBoardingId());

            originalBoarding = boarding.copy();
            originalPricing = new ArrayList<>(pricing);

            grooming.markAsApproved();
            boarding.updatePaymentStatus(PaymentStatus.PENDING);

            // Boarding pricing update
            var breakDown = BoardingPricingDomain.RequestBreakdown.createNew(
                    check.getId(),
                    check.getRequestType().getRequestType(),
                    grooming.getGroomingPrice(),
                    Instant.now()
            );
            pricing.add(breakDown);

            // Save all to DB
            requestUpdate.markRequestAsCompleted(check.getId());
            boardingUpdate.updateBoarding(boarding);
            pricingSearch.unwrappedUpdateRequestBreakdown(boarding.getId(), pricing);

            // DTO
            var dto = new RequestCompletedServiceDTO(
                    check.getId(),
                    check.getRequestType().getRequestType(),
                    RequestStatus.COMPLETED.getRequestStatus(),
                    Instant.now()
            );
            String petName = petSearch.getPetName(boarding.getPetId());
            String message = "The grooming for " + petName + " is completed.";

            return DomainResponse.success(dto, message);

        } catch (DomainArgumentException | PersistenceException e) {
            // Rollback
            log.warn("Initiating rollback for requestId: {}", request.getRequestId());
            try {
                if (originalBoarding != null && originalPricing != null) {
                    requestUpdate.rollbackAsActive(request.getRequestId());
                    boardingUpdate.updateBoarding(originalBoarding);
                    pricingSearch.unwrappedUpdateRequestBreakdown(originalBoarding.getId(), originalPricing);
                }
            } catch (Exception f) {
                log.error("Failed to delete photo request entry for requestId: {}", request.getRequestId(), f);
            }

            return DomainResponse.error(e.getMessage());
        }
    }


    private BoardingDomain validateActiveRequest(String boardingId) {
        var active = boardingSearch.findBoardingById(boardingId);
        if (!active.isSuccess())
            throw new DomainArgumentException(active.getMessage());
        if (!active.getData().isActive())
            throw new DomainArgumentException("Boarding of the request is inactive");
        return active.getData();
    }

    private void validateId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid id cannot be converted to ObjectId");
    }
}
