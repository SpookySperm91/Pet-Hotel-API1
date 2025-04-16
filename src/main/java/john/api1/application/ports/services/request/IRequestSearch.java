package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;
import john.api1.application.domain.models.request.RequestDomain;

import java.util.List;

public interface IRequestSearch {
    RequestDomain searchByRequestId(String requestId);
    List<RequestDomain> searchRequestByBoardingId(String boardingId);

    // Wrapped
    DomainResponse<RequestDomain> safeSearchById(String requestId);


    // Specific
    GroomingDomain searchGroomingById(String id);
    ExtensionDomain searchExtensionById(String id);
}
