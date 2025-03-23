package john.api1.application.domain.models;

import john.api1.application.components.enums.BucketType;

import java.time.Instant;

// Anemic Domain Object
// Immutable
public record MediaDomain(String id,
                          String ownerId,
                          String typeId,
                          String fileName,
                          BucketType bucketType,
                          String description,
                          Instant uploadedAt,
                          Instant preSignedUrlExpire,
                          boolean archived
) {

    public static MediaDomain create(String ownerId, String typeId, String fileUrl, BucketType bucketType, String description, Instant preSignedUrlExpire) {
        return new MediaDomain(
                null,
                ownerId,
                typeId,
                fileUrl,
                bucketType,
                description,
                Instant.now(),
                preSignedUrlExpire,
                false
        );
    }

    public static MediaDomain create(String ownerId, String fileUrl, BucketType bucketType, String description, Instant preSignedUrlExpire) {
        return new MediaDomain(
                null,
                ownerId,
                null,
                fileUrl,
                bucketType,
                description,
                Instant.now(),
                preSignedUrlExpire,
                false
        );
    }

    public static MediaDomain create(String ownerId, String fileUrl, BucketType bucketType, Instant preSignedUrlExpire) {
        return new MediaDomain(
                null,
                ownerId,
                null,
                fileUrl,
                bucketType,
                null,
                Instant.now(),
                preSignedUrlExpire,
                false
        );
    }

}

