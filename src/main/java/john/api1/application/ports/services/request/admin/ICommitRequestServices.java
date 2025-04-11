package john.api1.application.ports.services.request.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.request.admin.RequestCompleteServiceRDTO;

public interface ICommitRequestServices {
    DomainResponse<Void> commitExtensionRequest(RequestCompleteServiceRDTO request);
    DomainResponse<Void> commitGroomingRequest(RequestCompleteServiceRDTO request);
}
