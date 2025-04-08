package john.api1.application.domain.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;


public record CompletedPhotoRequestDomain(
        String id,
        String requestId,
        String ownerId,
        String mediaId,
        List<String> photosName,
        Instant uploadedAt) {
}
