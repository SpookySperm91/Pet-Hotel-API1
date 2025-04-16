package john.api1.application.services.aggregation;

import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.domain.models.request.VideoRequestDomain;
import john.api1.application.dto.mapper.request.commit.RequestCompletedPhotoDTO;
import john.api1.application.dto.mapper.request.commit.RequestCompletedVideoDTO;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class AggregationCompletedRequest implements IAggregationCompletedRequest {
    public RequestCompletedPhotoDTO completedPhotoRequest(PhotoRequestDomain domain, List<PreSignedUrlResponse> urls) {
        return new RequestCompletedPhotoDTO(
                domain.id(),
                domain.requestId(),
                domain.ownerId(),
                domain.photo(),
                urls,
                domain.uploadedAt());
    }

    public RequestCompletedVideoDTO completedVideoRequest(VideoRequestDomain domain, PreSignedUrlResponse url) {
        return new RequestCompletedVideoDTO(
                domain.id(),
                domain.requestId(),
                domain.ownerId(),
                domain.mediaId(),
                url.preSignedUrl(),
                domain.uploadedAt()
        );
    }

}
