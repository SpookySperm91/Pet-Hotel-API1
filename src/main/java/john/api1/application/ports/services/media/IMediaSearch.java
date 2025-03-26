package john.api1.application.ports.services.media;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.BucketType;
import john.api1.application.ports.repositories.wrapper.MediaPreview;

import java.time.Instant;
import java.util.List;

public interface IMediaSearch {

    // Find a single media file by its unique ID.
    DomainResponse<MediaPreview> findById(String id);

    // Fetch all media for a specific owner within a date range (e.g., by week).
    DomainResponse<List<MediaPreview>> findByOwnerIdAndUploadedBetween(String ownerId, Instant start, Instant end);

    // Retrieve all media associated with a specific owner.
    DomainResponse<List<MediaPreview>> findByOwnerId(String ownerId);

    //Get media of a certain type (e.g., PROFILE_PHOTO, PET_PHOTO) for a user within a date range.
    DomainResponse<List<MediaPreview>> findByOwnerIdAndBucketTypeAndUploadedBetween(String ownerId, BucketType bucketType, Instant start, Instant end);

    // Find media linked to a specific request
    DomainResponse<List<MediaPreview>> findByTypeId(String typeId);
}
