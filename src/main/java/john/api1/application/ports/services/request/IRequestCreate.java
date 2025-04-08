package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.request.RequestExtensionRDTO;
import john.api1.application.dto.request.request.RequestGroomingRDTO;
import john.api1.application.dto.request.request.RequestMediaRDTO;

public interface IRequestCreate {
    DomainResponse<String> createRequestMedia(RequestMediaRDTO request);
    DomainResponse<String> createRequestExtension(RequestExtensionRDTO request);
    DomainResponse<String> createRequestGrooming(RequestGroomingRDTO request);
}
