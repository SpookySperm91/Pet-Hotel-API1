package john.api1.application.adapters.repositories.request;

import john.api1.application.adapters.repositories.PhotoRequestEntity;
import john.api1.application.adapters.repositories.VideoRequestEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.domain.models.request.VideoRequestDomain;
import john.api1.application.ports.repositories.request.IRequestCompletedCreateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RequestCompletedCreateRepository implements IRequestCompletedCreateRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestCompletedCreateRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<String> createPhotoRequest(PhotoRequestDomain photo) {
        validateId(photo.requestId(), photo.ownerId());
        validateMediaId(photo.photo());


        var list = PhotoRequestEntity.map(photo.photo());
        PhotoRequestEntity entity = PhotoRequestEntity.create(
                new ObjectId(photo.requestId()),
                new ObjectId(photo.ownerId()),
                list);

        return Optional.ofNullable(mongoTemplate.save(entity))
                .map(PhotoRequestEntity::getId)
                .map(ObjectId::toString);
    }

    @Override
    public Optional<String> createVideoRequest(VideoRequestDomain video) {
        validateId(video.requestId(), video.ownerId());
        validateMediaId(video.mediaId());

        VideoRequestEntity entity = VideoRequestEntity.map(
                new ObjectId(video.requestId()),
                new ObjectId(video.ownerId()),
                new ObjectId(video.mediaId()),
                video.videoName()
        );

        return Optional.ofNullable(mongoTemplate.save(entity))
                .map(VideoRequestEntity::getId)
                .map(ObjectId::toString);
    }


    private void validateId(String requestId, String ownerId) {
        if (!ObjectId.isValid(requestId))
            throw new PersistenceException("Invalid request id cannot be converted to ObjectId");
        if (!ObjectId.isValid(ownerId))
            throw new PersistenceException("Invalid owner id cannot be converted to ObjectId");
    }

    private void validateMediaId(String mediaId) {
        if (!ObjectId.isValid(mediaId))
            throw new PersistenceException("Invalid media id cannot be converted to ObjectId");
    }

    private void validateMediaId(List<PhotoRequestDomain.MediaFile> mediaId) {
        for (PhotoRequestDomain.MediaFile id : mediaId) {
            if (!ObjectId.isValid(id.id())) {
                throw new PersistenceException("Invalid media id cannot be converted to ObjectId");
            }
        }
    }

}
