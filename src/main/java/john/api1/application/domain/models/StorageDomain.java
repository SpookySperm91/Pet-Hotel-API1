package john.api1.application.domain.models;

import john.api1.application.components.enums.BucketType;
import john.api1.application.components.enums.MediaType;
import java.time.Instant;

// Anemic Domain Object
// Immutable
public record StorageDomain(String id,
                            String ownerId,
                            String fileName,
                            BucketType bucketType,
                            String minioUrl,
                            Instant uploadedAt,
                            Instant preSignedUrlExpire,
                            MediaType mediaType,
                            boolean isPublic
) {
}

