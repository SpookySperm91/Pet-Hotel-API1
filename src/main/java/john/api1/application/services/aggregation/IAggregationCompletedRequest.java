package john.api1.application.services.aggregation;

import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.domain.models.request.VideoRequestDomain;
import john.api1.application.dto.mapper.request.commit.RequestCompletedPhotoDTO;
import john.api1.application.dto.mapper.request.commit.RequestCompletedVideoDTO;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;

import java.time.Instant;
import java.util.List;

public interface IAggregationCompletedRequest {
    public RequestCompletedPhotoDTO completedPhotoRequest(PhotoRequestDomain domain, List<PreSignedUrlResponse> urls);

    public RequestCompletedVideoDTO completedVideoRequest(VideoRequestDomain domain, PreSignedUrlResponse url);
}
