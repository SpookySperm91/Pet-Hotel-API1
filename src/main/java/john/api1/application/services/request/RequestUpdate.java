package john.api1.application.services.request;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.ports.repositories.request.IRequestDeleteRepository;
import john.api1.application.ports.repositories.request.IRequestSearchRepository;
import john.api1.application.ports.repositories.request.IRequestUpdateRepository;
import john.api1.application.ports.services.request.IRequestUpdate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = {DomainArgumentException.class, PersistenceException.class, MongoException.class})
public class RequestUpdate implements IRequestUpdate {
    private final IRequestUpdateRepository updateRepository;
    private final IRequestDeleteRepository deleteRepository;
    private final IRequestSearchRepository searchRepository;

    @Autowired
    public RequestUpdate(IRequestUpdateRepository updateRepository,
                         IRequestDeleteRepository deleteRepository,
                         IRequestSearchRepository searchRepository) {
        this.updateRepository = updateRepository;
        this.deleteRepository = deleteRepository;
        this.searchRepository = searchRepository;
    }

    // Mark request as completed
    // Constraints: Only works if such request is currently active
    @Override
    public DomainResponse<Void> markRequestAsCompleted(String requestId) {
        try {
            validateId(requestId);
            var request = searchRepository.findById(requestId);
            if (request.isEmpty()) return DomainResponse.error("Request cannot be found");

            RequestDomain archived = request.get();
            archived.markStatus(RequestStatus.COMPLETED);
            updateRepository.updateRequestStatusAndActive(archived.getId(), archived.getRequestStatus(), archived.isActive());

            return DomainResponse.success("Successfully marked request as completed");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Unable to complete the request. Please try again.");
        }
    }

    // Archived completed request
    @Override
    public DomainResponse<Void> archiveRequest(String requestId) {
        try {
            var request = searchRepository.findById(requestId);
            if (request.isEmpty()) return DomainResponse.error("Request cannot be found");

            RequestDomain archived = request.get();
            archived.markStatus(RequestStatus.ARCHIVED);
            updateRepository.updateRequestStatusAndActive(archived.getId(), archived.getRequestStatus(), archived.isActive());

            return DomainResponse.success("Request successfully archived");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Unable to complete the request. Please try again.");
        }
    }

    // Delete Rejected, Archived, Cancelled requests
    @Override
    public DomainResponse<Void> deleteRequest(String requestId) {
        try {
            validateId(requestId);
            var request = searchRepository.findById(requestId);
            if (request.isEmpty()) return DomainResponse.error("Request cannot be found");

            RequestDomain archived = request.get();
            if (!archived.deletable())
                return DomainResponse.error(String.format("Request is currently %s and cannot be deleted",
                        archived.getRequestType().getRequestType().toLowerCase()));

            deleteRepository.deleteById(new ObjectId(requestId));
            return DomainResponse.success("Request successfully deleted");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Unable to complete the request. Please try again.");
        }
    }

    // Rollback
    public void rollbackAsActive(String requestId){
        validateId(requestId);
        updateRepository.updateRequestStatusAndActive(requestId, RequestStatus.PENDING, true);
    }

    private void validateId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid request id cannot convert into ObjectId!");
    }
}
