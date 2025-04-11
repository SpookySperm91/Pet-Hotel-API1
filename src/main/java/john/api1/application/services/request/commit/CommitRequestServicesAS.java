package john.api1.application.services.request.commit;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.request.admin.RequestCompleteServiceRDTO;
import john.api1.application.ports.services.request.admin.ICommitRequestServices;
import org.springframework.stereotype.Service;

@Service
public class CommitRequestServicesAS implements ICommitRequestServices {



    @Override
    public DomainResponse<Void> commitExtensionRequest(RequestCompleteServiceRDTO request) {
        return DomainResponse.success();
    }

    @Override
    public DomainResponse<Void> commitGroomingRequest(RequestCompleteServiceRDTO request) {
        return DomainResponse.success();
    }
}
