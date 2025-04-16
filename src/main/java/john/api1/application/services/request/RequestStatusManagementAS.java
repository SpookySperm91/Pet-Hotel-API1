package john.api1.application.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.request.RequestStatusUpdateDTO;
import john.api1.application.ports.repositories.request.IRequestSearchRepository;
import john.api1.application.ports.repositories.request.IRequestUpdateRepository;
import john.api1.application.ports.services.IPetOwnerManagement;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestStatusManagement;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestStatusManagementAS implements IRequestStatusManagement {
    private final IRequestUpdateRepository updateRepository;
    private final IRequestSearchRepository searchRepository;
    private final IPetOwnerManagement ownerSearch;
    private final IPetSearch petSearch;

    @Autowired
    public RequestStatusManagementAS(IRequestUpdateRepository updateRepository,
                                     IRequestSearchRepository searchRepository,
                                     IPetOwnerManagement ownerSearch,
                                     IPetSearch petSearch
    ) {
        this.updateRepository = updateRepository;
        this.searchRepository = searchRepository;
        this.ownerSearch = ownerSearch;
        this.petSearch = petSearch;
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> approvedRequest(String requestId) {
        return updateStatus(requestId, RequestStatus.IN_PROGRESS, RequestDomain::isValidToApprove,
                "Successfully approved pending request");
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> rejectRequest(String requestId) {
        return updateStatus(requestId, RequestStatus.REJECTED, null,
                "Successfully rejected pending request. Can be undo for 5 minutes");
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> cancelRequest(String requestId) {
        return updateStatus(requestId, RequestStatus.CANCELLED, null,
                "Successfully cancelled pending request");
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> revertToPending(String requestId) {
        return updateStatus(requestId, RequestStatus.PENDING, RequestDomain::isValidToRevert,
                "Successfully reverted in-progress request to pending");
    }

    @Override
    public DomainResponse<RequestStatusUpdateDTO> undoReject(String requestId) {
        return updateStatus(requestId, RequestStatus.PENDING, RequestDomain::isValidToUndoReject,
                "Successfully undid rejected request back to pending");
    }


    private DomainResponse<RequestStatusUpdateDTO> updateStatus(String requestId, RequestStatus newStatus,
                                                                StatusValidator validator, String successMessage) {
        try {
            validateId(requestId);

            var requestOpt = searchRepository.findById(requestId);
            if (requestOpt.isEmpty()) return DomainResponse.error("Request does not exist.");

            var request = requestOpt.get();
            if (validator != null) validator.validate(request);

            request.markStatus(newStatus);
            var updated = updateRepository.updateRequestStatusReturnId(requestId, request.getRequestStatus());
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

    @FunctionalInterface
    private interface StatusValidator {
        void validate(RequestDomain request) throws DomainArgumentException;
    }
}
