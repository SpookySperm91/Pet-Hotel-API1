package john.api1.application.services.aggregation;

import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.domain.models.request.VideoRequestDomain;
import john.api1.application.dto.mapper.request.commit.RequestCommittedPhotoDTO;
import john.api1.application.dto.mapper.request.commit.RequestCommittedVideoDTO;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;

import java.util.List;

public interface IAggregationCompletedRequest {
    public RequestCommittedPhotoDTO completedPhotoRequest(PhotoRequestDomain domain, List<PreSignedUrlResponse> urls);

    public RequestCommittedVideoDTO completedVideoRequest(VideoRequestDomain domain, PreSignedUrlResponse url);
}
