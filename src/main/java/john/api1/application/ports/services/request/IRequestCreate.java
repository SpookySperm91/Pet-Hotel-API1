package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.RequestRDTO;

public interface IRequestCreate {
    DomainResponse<String> createRequest(RequestRDTO request);
}
