package john.api1.application.ports.repositories.wrapper;

import jakarta.annotation.Nullable;
import john.api1.application.components.enums.BucketType;
import lombok.ToString;

import java.time.Instant;


public record MediaEntityPreview(String id,
                                 @Nullable String description,
                                 BucketType bucketType,
                                 String fileName,  // Raw URL from MongoDB
                                 Instant uploadedAt,
                                 Instant expiredAt) {
}
