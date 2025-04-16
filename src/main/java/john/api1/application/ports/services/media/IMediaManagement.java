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

    DomainResponse<MediaPreview> updatePreSignedUrlExpire(String mediaId, String ownerId);

    DomainResponse<MediaPreview> updateTypeId(String mediaId, String ownerId, ObjectId newTypeId);

    DomainResponse<String> archiveMedia(String mediaId, String ownerId);

    DomainResponse<String> deleteMedia(String mediaId, String ownerId);

    void deleteMediasByRequest(String requestId);



    // Unsafe
    PreSignedUrlResponse unwrappedGenerateMediaFile(String ownerId, String fileName, BucketType bucketType);

    String unwrappedSaveMediaFile(MediaDomain media);

}
