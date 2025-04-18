package john.api1.application.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.RequestStatusDS;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.request.RequestStatusUpdateDTO;
import john.api1.application.ports.repositories.request.IRequestSearchRepository;
import john.api1.application.ports.repositories.request.IRequestUpdateRepository;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestStatusManagement;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestStatusManagementAS implements IRequestStatusManagement {
    private final IRequestUpdateRepository updateRepository;
    private final IRequestSearchRepository searchRepository;
    private final IPetOwnerSearch ownerSearch;
    private final IPetSearch petSearch;

    @Autowired
    public RequestStatusManagementAS(IRequestUpdateRepository updateRepository,
                                     IRequestSearchRepository searchRepository,
                                     IPetOwnerSearch ownerSearch,
                                     IPetSearch petSearch) {
        this.updateRepository = updateRepository;
        this.searchRepository = searchRepository;
        this.ownerSearch = ownerSearch;
        this.petSearch = petSearch;
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> approvedRequest(String requestId) {
        return updateStatus(requestId, RequestStatus.IN_PROGRESS, null, RequestStatusDS::isValidToApprove,
                "Successfully approved pending request");
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> rejectRequest(String requestId, String message) {
        // Use separate method to handle rejection with a message
        return rejectStatus(requestId, message);
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> cancelRequest(String requestId) {
        return updateStatus(requestId, RequestStatus.CANCELLED, null, RequestStatusDS::isValidToCancel,
                "Successfully cancelled pending request");
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> revertToPending(String requestId) {
        return updateStatus(requestId, RequestStatus.PENDING, null, RequestStatusDS::isValidToRevert,
                "Successfully reverted in-progress request to pending");
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> undoReject(String requestId) {
        return updateStatus(requestId, RequestStatus.PENDING, null, RequestStatusDS::isValidToUndoReject,
                "Successfully undid rejected request back to pending");
    }

    // Separate method to handle rejection
    private DomainResponse<RequestStatusUpdateDTO> rejectStatus(String requestId, String rejectMessage) {
        try {
            validateId(requestId);

            var requestOpt = searchRepository.findById(requestId);
            if (requestOpt.isEmpty()) return DomainResponse.error("Request does not exist.");

            var request = requestOpt.get();
            RequestStatusDS.isValidToReject(request);

            RequestStatusDS.markStatus(request, RequestStatus.REJECTED);
            RequestStatusDS.stateRejectionReason(request, rejectMessage);
            var updated = updateRepository.updateToRejectReturnId(requestId, RequestStatus.REJECTED, rejectMessage);

            if (updated.isEmpty())
                return DomainResponse.error("Request status update failed. No documents were updated.");

            var updatedRequest = updated.get();
            String ownerName = ownerSearch.getPetOwnerName(updatedRequest.ownerId());
            String petName = petSearch.getPetName(updatedRequest.petId());

            var dto = new RequestStatusUpdateDTO(requestId, RequestStatus.REJECTED.getRequestStatus(), ownerName, petName);
            return DomainResponse.success(dto, "Successfully rejected pending request. Can be undone for 5 minutes");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    // Refactored method for general status updates (for approved, cancelled, etc.)
    private DomainResponse<RequestStatusUpdateDTO> updateStatus(String requestId, RequestStatus newStatus, String rejectMessage,
                                                                StatusValidator validator, String successMessage) {
        try {
            validateId(requestId);

            var requestOpt = searchRepository.findById(requestId);
            if (requestOpt.isEmpty()) return DomainResponse.error("Request does not exist.");

            var request = requestOpt.get();
            validator.validate(request);

            RequestStatusDS.markStatus(request, newStatus);
            if (request.getRequestStatus() == RequestStatus.REJECTED)
                RequestStatusDS.stateRejectionReason(request, rejectMessage);

            var updated = requiresActiveFlagUpdate(request.getRequestStatus())
                    ? updateRepository.updateRequestStatusAndActiveReturnId(requestId, request.getRequestStatus(), request.isActive())
                    : updateRepository.updateRequestStatusReturnId(requestId, request.getRequestStatus());

            if (updated.isEmpty())
                return DomainResponse.error("Request status update failed. No documents were updated.");

            var updatedRequest = updated.get();
            String ownerName = ownerSearch.getPetOwnerName(updatedRequest.ownerId());
            String petName = petSearch.getPetName(updatedRequest.petId());

            var dto = new RequestStatusUpdateDTO(requestId, newStatus.getRequestStatus(), ownerName, petName);
            return DomainResponse.success(dto, successMessage);

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    private void validateId(String id) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid request id. Cannot be converted to ObjectId.");
    }

    private boolean requiresActiveFlagUpdate(RequestStatus status) {
        return status == RequestStatus.CANCELLED || status == RequestStatus.COMPLETED;
    }

    @FunctionalInterface
    private interface StatusValidator {
        void validate(RequestDomain request) throws DomainArgumentException;
    }
}
