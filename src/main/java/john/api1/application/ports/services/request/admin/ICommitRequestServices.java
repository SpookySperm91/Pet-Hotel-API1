package john.api1.application.ports.services.request.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.request.commit.RequestCommittedServiceDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteServiceRDTO;

public interface ICommitRequestServices {
    DomainResponse<RequestCommittedServiceDTO> commitExtensionRequest(RequestCompleteServiceRDTO request);
    DomainResponse<RequestCommittedServiceDTO> commitGroomingRequest(RequestCompleteServiceRDTO request);
}
