package john.api1.application.adapters.repositories.request;

import john.api1.application.adapters.repositories.PhotoRequestEntity;
import john.api1.application.adapters.repositories.VideoRequestEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.ports.repositories.request.IRequestCompletedDeleteRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RequestCompletedDeleteRepository implements IRequestCompletedDeleteRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestCompletedDeleteRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void deletePhotoByRequestId(String id) {
        var photoId = validateId(id);
        Query query = new Query(Criteria.where("requestId").is(photoId));
        long deletedCount = mongoTemplate.remove(query, PhotoRequestEntity.class).getDeletedCount();
        if (deletedCount == 0) {
            throw new PersistenceException("Failed to delete existing photo request record");
        }
    }

    @Override
    public void deleteVideoByRequestId(String id) {
        var videoId = validateId(id);
        Query query = new Query(Criteria.where("requestId").is(videoId));
        long deletedCount = mongoTemplate.remove(query, VideoRequestEntity.class).getDeletedCount();
        if (deletedCount == 0) {
            throw new PersistenceException("Failed to delete existing video request record");
        }
    }

    @Override
    public void deleteExtensionById(String id) {
        var extensionId = validateId(id);

    }

    @Override
    public void deleteGroomingById(String id) {
        var groomingId = validateId(id);


    }

    private ObjectId validateId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid id cannot be converted to ObjectId");
        return new ObjectId(id);
    }
}
