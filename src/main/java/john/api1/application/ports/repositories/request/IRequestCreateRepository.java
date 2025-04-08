package john.api1.application.ports.repositories.request;

import john.api1.application.domain.models.request.RequestDomain;

import java.util.Optional;

public interface IRequestCreateRepository {
    Optional<String> createRequest(RequestDomain request);
}
