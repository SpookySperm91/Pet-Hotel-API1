package john.api1.application.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
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

    // Check id if valid
    // Update and return RequestCQRS
    // Fetch names for DTO
    // Return response
    public DomainResponse<RequestStatusUpdateDTO> approvedRequest(String requestId) {
        try {
            validateId(requestId);

            var approved = updateRepository.updateRequestStatusReturnId(requestId, RequestStatus.IN_PROGRESS);
            if (approved.isEmpty())
                return DomainResponse.error("Request status update failed. No documents were updated.");

            String ownerName = ownerSearch.getPetOwnerName(approved.get().ownerId());
            String petName = petSearch.getPetName(approved.get().petId());
            var dto = new RequestStatusUpdateDTO(requestId, RequestStatus.IN_PROGRESS.getRequestStatus(), ownerName, petName);

            return DomainResponse.success(dto, "Successfully approved pending request");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    public DomainResponse<RequestStatusUpdateDTO> rejectRequest(String requestId) {
        try {
            validateId(requestId);

            var approved = updateRepository.updateRequestStatusReturnId(requestId, RequestStatus.REJECTED);
            if (approved.isEmpty())
                return DomainResponse.error("Request status update failed. No documents were updated.");

            String ownerName = ownerSearch.getPetOwnerName(approved.get().ownerId());
            String petName = petSearch.getPetName(approved.get().petId());
            var dto = new RequestStatusUpdateDTO(requestId, RequestStatus.REJECTED.getRequestStatus(), ownerName, petName);

            return DomainResponse.success(dto, "Successfully rejected pending request. Can be undo for 5 minutes");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    public DomainResponse<RequestStatusUpdateDTO> cancelRequest(String requestId) {
        try {
            validateId(requestId);

            var approved = updateRepository.updateRequestStatusReturnId(requestId, RequestStatus.CANCELLED);
            if (approved.isEmpty())
                return DomainResponse.error("Request status update failed. No documents were updated.");

            String ownerName = ownerSearch.getPetOwnerName(approved.get().ownerId());
            String petName = petSearch.getPetName(approved.get().petId());
            var dto = new RequestStatusUpdateDTO(requestId, RequestStatus.CANCELLED.getRequestStatus(), ownerName, petName);

            return DomainResponse.success(dto, "Successfully cancelled pending request");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    public DomainResponse<RequestStatusUpdateDTO> revertToPending(String requestId) {
        try {
            validateId(requestId);

            var domain = searchRepository.findById(requestId);
            if (domain.isEmpty())
                return DomainResponse.error("Request data cannot be found.");
            if (domain.get().getRequestStatus() != RequestStatus.IN_PROGRESS) {
                return DomainResponse.error("Cannot revert. Request status is not in progress.");
            }

            // Update status
            domain.get().markStatus(RequestStatus.PENDING);
            var approved = updateRepository.updateRequestStatusReturnId(requestId, domain.get().getRequestStatus());
            if (approved.isEmpty())
                return DomainResponse.error("Request status update failed. No documents were updated.");

            // DTO
            String ownerName = ownerSearch.getPetOwnerName(approved.get().ownerId());
            String petName = petSearch.getPetName(approved.get().petId());
            var dto = new RequestStatusUpdateDTO(requestId, domain.get().getRequestStatus().getRequestStatus(), ownerName, petName);

            return DomainResponse.success(dto, "Successfully revert in-progress request to pending");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    public DomainResponse<RequestStatusUpdateDTO> undoReject(String requestId) {
        try {
            validateId(requestId);

            var domain = searchRepository.findById(requestId);
            if (domain.isEmpty())
                return DomainResponse.error("Request data cannot be found.");
            if (domain.get().getRequestStatus() != RequestStatus.REJECTED) {
                return DomainResponse.error("Cannot undo reject. The current status is not rejected.");
            }

            // Update status
            domain.get().markStatus(RequestStatus.PENDING);
            var approved = updateRepository.updateRequestStatusReturnId(requestId, domain.get().getRequestStatus());
            if (approved.isEmpty())
                return DomainResponse.error("Request status update failed. No documents were updated.");

            // DTO
            String ownerName = ownerSearch.getPetOwnerName(approved.get().ownerId());
            String petName = petSearch.getPetName(approved.get().petId());
            var dto = new RequestStatusUpdateDTO(requestId, domain.get().getRequestStatus().getRequestStatus(), ownerName, petName);

            return DomainResponse.success(dto, "Successfully undo to rejected request back to pending");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }


    private void validateId(String id) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid request id cannot converted to ObjectId");
    }
}
