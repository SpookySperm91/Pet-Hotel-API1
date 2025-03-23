package john.api1.application.ports.repositories.records;

import jakarta.annotation.Nullable;
import john.api1.application.components.enums.BucketType;

import java.time.Instant;

public record MediaPreview(
        String id,
        @Nullable
        String description,
        BucketType bucketType,
        String preSignedUrl,
        Instant expiredAt) {
}
