package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;

public interface IRequestUpdate {
    DomainResponse<Void> markRequestAsCompleted(String requestId);
    DomainResponse<Void> archiveRequest(String requestId);
    DomainResponse<Void> deleteRequest(String requestId);
}
