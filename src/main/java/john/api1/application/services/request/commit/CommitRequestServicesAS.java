package john.api1.application.services.request.commit;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.components.exception.PersistenceHistoryException;
import john.api1.application.domain.cores.RequestStatusDS;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.dto.mapper.request.commit.RequestCompletedServiceDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteServiceRDTO;
import john.api1.application.ports.repositories.boarding.IBoardingManagementRepository;
import john.api1.application.ports.repositories.request.IRequestCompletedUpdateRepository;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import john.api1.application.ports.services.boarding.IPricingManagement;
import john.api1.application.ports.services.history.IHistoryLogCreate;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestSearch;
import john.api1.application.ports.services.request.IRequestUpdate;
import john.api1.application.ports.services.request.admin.ICommitRequestServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(rollbackFor = {DomainArgumentException.class, PersistenceException.class, MongoException.class})
public class CommitRequestServicesAS implements ICommitRequestServices {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CommitRequestServicesAS.class);

    private final IBoardingManagementRepository boardingUpdate;
    private final IRequestCompletedUpdateRepository serviceUpdate;
    private final IPricingManagement pricingSearch;
    private final IRequestUpdate requestUpdate;
    private final IBoardingSearch boardingSearch;
    private final IRequestSearch requestSearch;
    private final IPetSearch petSearch;
    private final IPetOwnerSearch ownerSearch;
    private final IHistoryLogCreate historyLog;


    @Autowired
    public CommitRequestServicesAS(IBoardingManagementRepository boardingUpdate,
                                   IRequestCompletedUpdateRepository serviceUpdate,
                                   IPricingManagement pricingSearch,
                                   IRequestUpdate requestUpdate,
                                   IBoardingSearch boardingSearch,
                                   IRequestSearch requestSearch,
                                   IPetSearch petSearch,
                                   IPetOwnerSearch ownerSearch,
                                   IHistoryLogCreate historyLog) {
        this.boardingUpdate = boardingUpdate;
        this.serviceUpdate = serviceUpdate;
        this.pricingSearch = pricingSearch;
        this.requestUpdate = requestUpdate;
        this.requestSearch = requestSearch;
        this.boardingSearch = boardingSearch;
        this.petSearch = petSearch;
        this.ownerSearch = ownerSearch;
        this.historyLog = historyLog;
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
        try {
            validateId(request.getRequestId());   // CHECK

            var check = requestSearch.searchByRequestId(request.getRequestId());   // CHECK
            var boarding = validateActiveRequest(check.getBoardingId());  // CHECK
            var extension = requestSearch.searchExtensionByRequestId(request.getRequestId());  // CHECK
            var pricing = pricingSearch.getBreakdown(check.getBoardingId());  // CHECK

            // Check request status
            RequestStatusDS.isValidToCommit(check);

            // Approve extension
            // Add reply message to request
            // Update boarding
            extension.markAsApproved();
            check.setResponseMessage(request.getNotes());
            boarding.extendBoarding(extension.getExtendedHours());
            boarding.daycareToLongDay();
            boarding.updatePaymentStatus(PaymentStatus.PENDING);
            boarding.updateBoardingStatus(BoardingStatus.BOARDING);

            // Boarding pricing update;
            addPricingBreakdown(pricing, check.getId(), check.getRequestType().getRequestType(), extension.getAdditionalPrice());

            // Save all to DB
            serviceUpdate.updateApprovalExtension(extension.getId(), extension.isApproved(), extension.getUpdatedAt());   // CHECK
            requestUpdate.markRequestAsCompletedWithMessage(check.getId(), check.getResponseMessage());  // CHECK
            boardingUpdate.updateBoarding(boarding);    // CHECK
            pricingSearch.unwrappedUpdateRequestBreakdown(boarding.getId(), pricing);    // CHECK

            // DTO
            String petName = petSearch.getPetName(boarding.getPetId());
            String ownerName = ownerSearch.getPetOwnerName(boarding.getOwnerId());
            String message = "The boarding extension for " + petName + " is completed.";
            var dto = new RequestCompletedServiceDTO(
                    check.getId(),
                    check.getRequestType().getRequestType(),
                    RequestStatus.COMPLETED.getRequestStatus(),
                    Instant.now()
            );

            // History log
            try {
                historyLog.createActivityLogRequest(check, ownerName, petName);
                log.info("Activity log created for completing extension request made by '{}'", ownerName);
            } catch (PersistenceHistoryException e) {
                log.warn("Activity log for completing extension failed to save in class 'CommitRequestServicesAS'");
            }

            return DomainResponse.success(dto, message);

        } catch (DomainArgumentException | PersistenceException e) {
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

        try {
            validateId(request.getRequestId());

            var check = requestSearch.searchByRequestId(request.getRequestId());
            var boarding = validateActiveRequest(check.getBoardingId());
            var grooming = requestSearch.searchGroomingByRequestId(request.getRequestId());
            var pricing = pricingSearch.getBreakdown(check.getBoardingId());

            // Check request status
            RequestStatusDS.isValidToCommit(check);

            // Approve extension
            // Add reply message to request
            // Update payment status
            grooming.markAsApproved();
            check.setResponseMessage(request.getNotes());
            boarding.updatePaymentStatus(PaymentStatus.PENDING);

            // Boarding pricing update
            addPricingBreakdown(pricing, check.getId(), check.getRequestType().getRequestType(), grooming.getGroomingPrice());

            // Save all to DB
            serviceUpdate.updateApprovalExtension(grooming.getId(), grooming.isApproved(), grooming.getUpdatedAt());
            requestUpdate.markRequestAsCompletedWithMessage(check.getId(), check.getResponseMessage());
            boardingUpdate.updateBoarding(boarding);
            pricingSearch.unwrappedUpdateRequestBreakdown(boarding.getId(), pricing);

            // DTO
            String petName = petSearch.getPetName(boarding.getPetId());
            String ownerName = ownerSearch.getPetOwnerName(boarding.getOwnerId());
            String message = "The grooming for " + petName + " is completed.";
            var dto = new RequestCompletedServiceDTO(
                    check.getId(),
                    check.getRequestType().getRequestType(),
                    RequestStatus.COMPLETED.getRequestStatus(),
                    Instant.now()
            );

            // History log
            try {
                historyLog.createActivityLogRequest(check, ownerName, petName);
                log.info("Activity log created for completing grooming request made by '{}'", ownerName);
            } catch (PersistenceHistoryException e) {
                log.warn("Activity log for completing grooming failed to save in class 'CommitRequestServicesAS'");
            }

            return DomainResponse.success(dto, message);

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    private void addPricingBreakdown(List<BoardingPricingDomain.RequestBreakdown> pricing, String requestId, String requestType, double amount) {
        var breakDown = BoardingPricingDomain.RequestBreakdown.createNew(requestId, requestType, amount);
        pricing.add(breakDown);
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
