package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.request.RequestExtensionCreatedDTO;
import john.api1.application.dto.mapper.request.RequestGroomingCreatedDTO;
import john.api1.application.dto.mapper.request.RequestMediaCreatedDTO;
import john.api1.application.dto.request.request.RequestExtensionRDTO;
import john.api1.application.dto.request.request.RequestGroomingRDTO;
import john.api1.application.dto.request.request.RequestMediaRDTO;

public interface IRequestCreate {
    DomainResponse<RequestMediaCreatedDTO> createRequestMedia(RequestMediaRDTO request);
    DomainResponse<RequestExtensionCreatedDTO> createRequestExtension(RequestExtensionRDTO request);
    DomainResponse<RequestGroomingCreatedDTO> createRequestGrooming(RequestGroomingRDTO request);
}
