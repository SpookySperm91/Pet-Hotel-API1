package john.api1.application.ports.repositories.request;

import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;

import java.time.Instant;

public interface IRequestCompletedUpdateRepository {
    void updateApprovalExtension(String id, boolean approval, Instant updatedAt);
    void updateApprovalGrooming(String id, boolean approval, Instant updatedAt);

    void updateExtension(ExtensionDomain domain);
    void updateGrooming(GroomingDomain domain);
}
