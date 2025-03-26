package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;

public interface IRequestUpdate {
    DomainResponse<String> markRequestAsCompleted();
    DomainResponse<String> archiveRequest();
    DomainResponse<String> deleteArchivedRequest();
    DomainResponse<String> deleteRejectedRequest();
}
