package john.api1.application.domain.models;

import john.api1.application.components.enums.BucketType;
import java.time.Instant;

// Anemic Domain Object
// Immutable
public record MediaDomain(String id,
                          String ownerId,
                          String typeId,
                          String fileUrl,
                          BucketType bucketType,
                          String description,
                          Instant uploadedAt,
                          Instant preSignedUrlExpire,
                          boolean archived
) {
}

