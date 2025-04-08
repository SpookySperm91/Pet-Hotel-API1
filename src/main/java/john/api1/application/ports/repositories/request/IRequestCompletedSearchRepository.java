package john.api1.application.ports.repositories.request;

import john.api1.application.domain.models.request.ExtensionDomain;

import java.util.List;

public interface IRequestCompletedSearchRepository {
    List<ExtensionDomain> getExtensionByCurrentBoarding(String boardingId);
}
