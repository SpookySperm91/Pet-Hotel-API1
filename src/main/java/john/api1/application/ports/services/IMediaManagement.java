package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.BucketType;
import john.api1.application.domain.models.MediaDomain;
import john.api1.application.ports.repositories.records.MediaPreview;
import john.api1.application.ports.repositories.records.PreSignedUrlResponse;
import org.bson.types.ObjectId;
import org.springframework.expression.spel.ast.OpAnd;

import java.util.Optional;

public interface IMediaManagement {
    // return url link
    DomainResponse<PreSignedUrlResponse> generateMediaFile(String ownerId, String fileName, BucketType bucketType);

    DomainResponse<MediaPreview> saveMediaFile(MediaDomain media);

    DomainResponse<MediaPreview> updatePreSignedUrlExpire(ObjectId mediaId, ObjectId ownerId);

    DomainResponse<MediaPreview> updateTypeId(ObjectId mediaId, ObjectId ownerId, ObjectId newTypeId);

    DomainResponse<String> archiveMedia(ObjectId mediaId, ObjectId ownerId);

    DomainResponse<String> deleteMedia(ObjectId mediaId, ObjectId ownerId);
}
