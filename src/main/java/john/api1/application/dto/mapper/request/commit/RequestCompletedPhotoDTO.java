package john.api1.application.dto.mapper.request.commit;

import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;

import java.time.Instant;
import java.util.List;

public record RequestCompletedPhotoDTO(
        String id,
        String requestId,
        String ownerId,
        List<PhotoRequestDomain.MediaFile> photo,
        List<PreSignedUrlResponse> urls,
        Instant createdAt
) {
}
