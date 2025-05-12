package john.api1.application.services.boarding;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.components.exception.PersistenceHistoryException;
import john.api1.application.domain.cores.boarding.BoardingExtensionDS;
import john.api1.application.domain.cores.boarding.BoardingManagementDS;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.dto.mapper.boarding.BoardingDTO;
import john.api1.application.dto.request.BoardingStatusRDTO;
import john.api1.application.dto.request.PaymentStatusDTO;
import john.api1.application.ports.repositories.boarding.IBoardingManagementRepository;
import john.api1.application.ports.repositories.boarding.IBoardingSearchRepository;
import john.api1.application.ports.repositories.request.IRequestCompletedSearchRepository;
import john.api1.application.ports.services.IBoardingAggregation;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.boarding.IBoardingManagement;
import john.api1.application.ports.services.boarding.IPricingManagement;
import john.api1.application.ports.services.history.IHistoryLogCreate;
import john.api1.application.ports.services.pet.IPetUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

// THIS APPLICATION SERVICE IS FOR UPDATING CURRENT BOARDING
@Service
public class BoardingManagementAS implements IBoardingManagement {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BoardingManagementAS.class);

    private final IBoardingSearchRepository boardingSearch;
    private final IBoardingManagementRepository boardingManagement;
    private final IRequestCompletedSearchRepository requestManagement;
    private final IPricingManagement pricingManagement;
    private final IPetUpdate petUpdate;
    private final IPetOwnerSearch ownerSearch;
    private final IHistoryLogCreate historyLog;
    private final IBoardingAggregation aggregation;


    @Autowired
    public BoardingManagementAS(// repositories
                                IBoardingSearchRepository boardingSearch,
                                IBoardingManagementRepository boardingManagement,
                                IRequestCompletedSearchRepository requestManagement,
                                // services
                                IPricingManagement pricingManagement,
                                IPetUpdate petUpdate,
                                IPetOwnerSearch ownerSearch,
                                IHistoryLogCreate historyLog,
                                IBoardingAggregation aggregation) {
        this.boardingSearch = boardingSearch;
        this.boardingManagement = boardingManagement;
        this.requestManagement = requestManagement;
        this.pricingManagement = pricingManagement;
        this.petUpdate = petUpdate;
        this.ownerSearch = ownerSearch;
        this.historyLog = historyLog;
        this.aggregation = aggregation;
    }

    // Validate current boarding status
    // Update boarding status before release
    // Update boarding DB
    // Retrieve boarding details for aggregation return data
    @Override
    public DomainResponse<BoardingDTO> releasedBoarding(String boardingId) {
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
    public DomainResponse<BoardingDTO> forceReleasedBoarding(String boardingId) {
        var boarding = boardingSearch.searchById(boardingId);
        if (boarding.isEmpty()) return DomainResponse.error("Boarding do not exist");

        // Cannot release if not paid
        if (boarding.get().getBoardingStatus() == BoardingStatus.RELEASED)
            return DomainResponse.error("Boarding is already released");
        return release(boarding.get(), boardingId);
    }


    private DomainResponse<BoardingDTO> release(BoardingDomain boarding, String boardingId) {
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
            var extensions = requestManagement.getExtensionByCurrentBoarding(boarding.getId());
            Instant extendedTotalTime = BoardingExtensionDS.calculateFinalBoardingEnd(boarding.getBoardingEnd(), extensions);

            // Duration
            var hours = BoardingManagementDS.calculateBoardingDurationHours(boarding.getBoardingStart(), extendedTotalTime);
            var days = BoardingManagementDS.calculateBoardingDurationDays(boarding.getBoardingStart(), extendedTotalTime);

            // DTO
            Instant now = Instant.now();
            String message = String.format(
                    "%s's pet '%s' is successfully released from boarding at %s"
                    , ownerDetail.ownerName(), petUpdated.getData().petName(), now);
            var dto = aggregation.boardingReleasedAggregation(boarding, boardingPrice.getData(), ownerDetail, petUpdated.getData(), days, hours, extendedTotalTime, now);

            // History log
            try {
                historyLog.createActivityLogBoarding(boarding, ownerDetail.ownerName(), petUpdated.getData().petName());
                log.info("Activity log created for boarding release for pet '{}'", petUpdated.getData().petName());
            } catch (PersistenceHistoryException e) {
                log.warn("Activity log for boarding release failed to save in class 'BoardingManagementAS'");
            }

            return DomainResponse.success(dto, message);
        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    // Update payment status
    @Override
    public DomainResponse<Void> updatePaidStatus(PaymentStatusDTO paymentStatus) {
        try {
            // Check boarding status
            var boardingStatusOpt = boardingSearch.checkBoardingCurrentStatus(paymentStatus.getId());
            if (boardingStatusOpt.isEmpty()) {
                return DomainResponse.error("Boarding status cannot be found");
            }

            if (boardingStatusOpt.get().equals(BoardingStatus.RELEASED)) {
                return DomainResponse.error("Boarding is already released. It cannot be updated!");
            }
            var status = PaymentStatus.safeFromStringOrDefault(paymentStatus.getStatus());
            boardingManagement.updatePaidStatus(paymentStatus.getId(), status);

            return DomainResponse.success("Successfully updated payment status to '" + status.getPaymentStatus() + "'");
        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("There was an issue with the database. Please try again.");
        }
    }

    // Update boarding status
    @Override
    public DomainResponse<Void> updateBoardingStatus(BoardingStatusRDTO boardingStatus) {
        try {
            // Check boarding status
            var boardingStatusOpt = boardingSearch.checkBoardingCurrentStatus(boardingStatus.getId());
            if (boardingStatusOpt.isEmpty()) {
                return DomainResponse.error("Boarding status cannot be found");
            }

            if (boardingStatusOpt.get().equals(BoardingStatus.RELEASED)) {
                return DomainResponse.error("Boarding is already released. It cannot be updated!");
            }

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
}
