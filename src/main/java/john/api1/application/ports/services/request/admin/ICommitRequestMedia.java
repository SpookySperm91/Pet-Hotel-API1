package john.api1.application.ports.services.request.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.request.commit.RequestCommittedPhotoDTO;
import john.api1.application.dto.mapper.request.commit.RequestCommittedVideoDTO;
import john.api1.application.dto.request.request.admin.RequestCompletePhotoRDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteVideoRDTO;

public interface ICommitRequestMedia {
    DomainResponse<RequestCommittedPhotoDTO> commitPhotoRequest(RequestCompletePhotoRDTO request);
    DomainResponse<RequestCommittedVideoDTO> commitVideoRequest(RequestCompleteVideoRDTO request);
}
