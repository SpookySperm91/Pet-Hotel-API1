package john.api1.application.ports.services.request.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.request.commit.RequestCompletedPhotoDTO;
import john.api1.application.dto.mapper.request.commit.RequestCompletedVideoDTO;
import john.api1.application.dto.request.request.admin.RequestCompletePhotoRDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteVideoRDTO;

public interface ICommitRequestMedia {
    DomainResponse<RequestCompletedPhotoDTO> commitPhotoRequest(RequestCompletePhotoRDTO request);
    DomainResponse<RequestCompletedVideoDTO> commitVideoRequest(RequestCompleteVideoRDTO request);
}
