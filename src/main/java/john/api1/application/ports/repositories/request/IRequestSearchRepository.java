package john.api1.application.ports.repositories.request;

import john.api1.application.domain.models.boarding.ExtensionDomain;

import java.util.List;

public interface IRequestSearchRepository {
    List<ExtensionDomain> getExtensionByCurrentBoarding(String boardingId);
}
