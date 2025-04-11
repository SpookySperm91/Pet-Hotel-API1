package john.api1.application.domain.models.request;

import java.time.Instant;
import java.util.List;


public record PhotoRequestDomain(
        String id,
        String requestId,
        String ownerId,
        List<MediaFile> photo,
        Instant uploadedAt) {

    public static PhotoRequestDomain create(String requestId, String ownerId, List<MediaFile> photosName) {
        return new PhotoRequestDomain(null, requestId, ownerId, photosName, Instant.now());
    }

    public record MediaFile(
            String id, String name
    ) {
    }
}
