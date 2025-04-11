package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.domain.models.request.RequestDomain;

import java.util.List;
import java.util.Optional;

public interface IRequestSearch {
    RequestDomain searchById(String requestId);
    List<RequestDomain> searchRequestByBoardingId(String boardingId);

    // Wrapped
    DomainResponse<RequestDomain> safeSearchById(String requestId);
}
