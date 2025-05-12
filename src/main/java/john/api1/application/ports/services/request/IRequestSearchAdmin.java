package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.request.search.RequestSearchDTO;

import java.util.List;

public interface IRequestSearchAdmin {
    DomainResponse<List<RequestSearchDTO>> searchAllInProgress();

    DomainResponse<List<RequestSearchDTO>> searchAllPending();

    DomainResponse<List<RequestSearchDTO>> searchAllCompleted();

    DomainResponse<List<RequestSearchDTO>> searchAllRejected();
}
