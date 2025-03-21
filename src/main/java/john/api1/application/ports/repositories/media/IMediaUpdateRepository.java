package john.api1.application.ports.repositories.media;
import org.bson.types.ObjectId;
import java.time.Instant;
public interface IMediaUpdateRepository {

    // Update pre-signed URL expiration
    boolean updatePreSignedUrlExpire(ObjectId mediaId, ObjectId ownerId, Instant newExpiration);

    // Reassign media to a different request (update typeId)
    boolean updateTypeId(ObjectId mediaId, ObjectId ownerId, ObjectId newTypeId);

    // Archive media
    boolean archiveMedia(ObjectId mediaId, ObjectId ownerId);

    // Delete media permanently from DB
    boolean deleteMedia(ObjectId mediaId, ObjectId ownerId);
}


// id: id of that file
// owner id: owner id of that file (owner id, pet account id for profile photo)
// type id: request id of that file