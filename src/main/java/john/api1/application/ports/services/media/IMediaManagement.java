package john.api1.application.ports.services.media;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.BucketType;
import john.api1.application.domain.models.MediaDomain;
import john.api1.application.ports.repositories.wrapper.MediaPreview;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;
import org.bson.types.ObjectId;

public interface IMediaManagement {
    // return url link
    String generateMediaObjectName(String name, String id);

    DomainResponse<PreSignedUrlResponse> generateMediaFile(String ownerId, String fileName, BucketType bucketType);

    DomainResponse<MediaPreview> saveMediaFile(MediaDomain media);

    DomainResponse<MediaPreview> updatePreSignedUrlExpire(ObjectId mediaId, ObjectId ownerId);

    DomainResponse<MediaPreview> updateTypeId(ObjectId mediaId, ObjectId ownerId, ObjectId newTypeId);

    DomainResponse<String> archiveMedia(ObjectId mediaId, ObjectId ownerId);

    DomainResponse<String> deleteMedia(ObjectId mediaId, ObjectId ownerId);
}
