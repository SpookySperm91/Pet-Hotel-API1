package john.api1.application.ports.repositories.request;

import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.domain.models.request.RequestDomain;

import java.util.Optional;

public interface IRequestUpdateRepository {
    void updateRequestStatus(String id, RequestStatus status);

    void updateRequestStatusAndActive(String id, RequestStatus status, boolean active);

    Optional<RequestDomain> updateAfterReject(String id, RequestStatus status, String rejectedDescription);
}
