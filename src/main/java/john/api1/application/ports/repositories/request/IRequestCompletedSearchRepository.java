package john.api1.application.ports.repositories.request;

import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;

import java.util.List;
import java.util.Optional;

public interface IRequestCompletedSearchRepository {
    List<ExtensionDomain> getExtensionByCurrentBoarding(String boardingId);

    Optional<GroomingDomain> getGroomingByRequestId(String id);

    Optional<ExtensionDomain> getExtensionByRequestId(String id);


    // CQRS
    Optional<GroomingCQRS> getGroomingByRequestIdCqrs(String id);
    Optional<ExtensionCQRS> getExtensionByRequestIdCqrs(String id);
}
