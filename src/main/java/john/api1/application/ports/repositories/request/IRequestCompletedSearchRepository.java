package john.api1.application.ports.repositories.request;

import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;

import java.util.List;
import java.util.Optional;

public interface IRequestCompletedSearchRepository {
    List<ExtensionDomain> getExtensionByCurrentBoarding(String boardingId);

    Optional<GroomingDomain> getGroomingById(String id);

    Optional<ExtensionDomain> getExtensionById(String id);

}
