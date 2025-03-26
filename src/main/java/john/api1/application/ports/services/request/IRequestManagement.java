package john.api1.application.ports.services.request;

import john.api1.application.components.DomainResponse;

public interface IRequestManagement {
    DomainResponse<String> cancelRequest();
    DomainResponse<String> approvedRequest();
    DomainResponse<String> rejectRequest();
    DomainResponse<String> processRequest();
    DomainResponse<String> revertToPending();
}
