package john.api1.application.ports.repositories.wrapper;

import jakarta.annotation.Nullable;
import john.api1.application.components.enums.BucketType;

import java.time.Instant;
import java.util.List;

public record MediaPreview(
        String id,
        @Nullable
        String description,
        BucketType bucketType,
        String preSignedUrl,
        Instant expiredAt) {

    public record MediaPreviewDTO(
            String id,
            String preSignedUrl,
            Instant expiredAt
    ) {
        public static List<MediaPreviewDTO> map(List<MediaPreview> preview) {
            return preview.stream()
                    .map(p -> new MediaPreviewDTO(p.id(), p.preSignedUrl(), p.expiredAt()))
                    .toList();
        }
    }
}
