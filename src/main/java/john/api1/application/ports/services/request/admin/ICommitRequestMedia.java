package john.api1.application.ports.services.request.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.request.admin.RequestCompletePhotoRDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteVideoRDTO;

public interface ICommitRequestMedia {
    DomainResponse<Void> commitPhotoRequest(RequestCompletePhotoRDTO request);
    DomainResponse<Void> commitVideoRequest(RequestCompleteVideoRDTO request);
}
