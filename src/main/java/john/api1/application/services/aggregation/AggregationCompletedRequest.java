package john.api1.application.services.aggregation;

import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.domain.models.request.VideoRequestDomain;
import john.api1.application.dto.mapper.request.commit.RequestCommittedPhotoDTO;
import john.api1.application.dto.mapper.request.commit.RequestCommittedVideoDTO;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AggregationCompletedRequest implements IAggregationCompletedRequest {
    public RequestCommittedPhotoDTO completedPhotoRequest(PhotoRequestDomain domain, List<PreSignedUrlResponse> urls) {
        return new RequestCommittedPhotoDTO(
                domain.id(),
                domain.requestId(),
                domain.ownerId(),
                domain.photo(),
                urls,
                domain.uploadedAt());
    }

    public RequestCommittedVideoDTO completedVideoRequest(VideoRequestDomain domain, PreSignedUrlResponse url) {
        return new RequestCommittedVideoDTO(
                domain.id(),
                domain.requestId(),
                domain.ownerId(),
                domain.mediaId(),
                url.preSignedUrl(),
                domain.uploadedAt()
        );
    }

}
