package john.api1.application.domain.models.request;

import java.time.Instant;


public record VideoRequestDomain(
        String id,
        String requestId,
        String ownerId,
        String mediaId,
        String videoName,
        Instant uploadedAt) {

    public static VideoRequestDomain create(String requestId, String ownerId, String mediaId, String videoName) {
        return new VideoRequestDomain(null, requestId, ownerId, mediaId, videoName, Instant.now());
    }

    public VideoRequestDomain mapWithId(String id) {
        return new VideoRequestDomain(id, requestId, ownerId, mediaId, videoName, uploadedAt);
    }
}
