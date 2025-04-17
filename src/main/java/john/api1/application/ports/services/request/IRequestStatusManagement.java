package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.request.RequestStatusUpdateDTO;
import john.api1.application.ports.repositories.request.RequestCQRS;

public interface IRequestStatusManagement {
    DomainResponse<RequestStatusUpdateDTO> approvedRequest(String requestId);
    DomainResponse<RequestStatusUpdateDTO> rejectRequest(String requestId, String message);
    DomainResponse<RequestStatusUpdateDTO> cancelRequest(String requestId);     // by owner or admin(?)
    DomainResponse<RequestStatusUpdateDTO> revertToPending(String requestId);
    DomainResponse<RequestStatusUpdateDTO> undoReject(String requestId);
}
