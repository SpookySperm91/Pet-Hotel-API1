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

    public static MediaDomain create(String ownerId, String typeId, String fileName, BucketType bucketType, String description, Instant preSignedUrlExpire) {
        return new MediaDomain(
                null,
                ownerId,
                typeId,
                fileName,
                bucketType,
                description,
                Instant.now(),
                preSignedUrlExpire,
                false
        );
    }

    public static MediaDomain create(String ownerId, String fileName, BucketType bucketType, String description, Instant preSignedUrlExpire) {
        return new MediaDomain(
                null,
                ownerId,
                null,
                fileName,
                bucketType,
                description,
                Instant.now(),
                preSignedUrlExpire,
                false
        );
    }

    public static MediaDomain create(String ownerId, String fileName, BucketType bucketType, Instant preSignedUrlExpire) {
        return new MediaDomain(
                null,
                ownerId,
                null,
                fileName,
                bucketType,
                null,
                Instant.now(),
                preSignedUrlExpire,
                false
        );
    }

}

