package john.api1.application.ports.services.request.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.request.commit.RequestCompletedServiceDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteServiceRDTO;

public interface ICommitRequestServices {
    DomainResponse<RequestCompletedServiceDTO> commitExtensionRequest(RequestCompleteServiceRDTO request);
    DomainResponse<RequestCompletedServiceDTO> commitGroomingRequest(RequestCompleteServiceRDTO request);
}
