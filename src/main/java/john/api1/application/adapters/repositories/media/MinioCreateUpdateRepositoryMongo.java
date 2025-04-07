package john.api1.application.adapters.repositories.media;

import jakarta.annotation.Nullable;
import john.api1.application.adapters.repositories.MinioEntity;
import john.api1.application.components.enums.BucketType;
import john.api1.application.domain.models.MediaDomain;
import john.api1.application.ports.repositories.media.IMediaCreateRepository;
import john.api1.application.ports.repositories.media.IMediaUpdateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class MinioCreateUpdateRepositoryMongo implements IMediaCreateRepository, IMediaUpdateRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MinioCreateUpdateRepositoryMongo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<MediaDomain> save(MediaDomain domain) {
        if (!ObjectId.isValid(domain.ownerId()) ||
                domain.typeId() != null && !ObjectId.isValid(domain.typeId())) {    // if not null, check if valid
            return Optional.empty();
        }

        MinioEntity entity = new MinioEntity(
                null,
                new ObjectId(domain.ownerId()),
                domain.typeId() != null ? new ObjectId(domain.typeId()) : null,
                domain.fileName(),
                domain.bucketType().getBucketType(),
                domain.description(),
                domain.uploadedAt(),
                domain.preSignedUrlExpire(),
                false
        );

        MinioEntity savedEntity = mongoTemplate.save(entity);
        return Optional.of(
                new MediaDomain(
                        savedEntity.getId().toString(),
                        savedEntity.getOwnerId().toString(),
                        savedEntity.getTypeId() != null ? savedEntity.getTypeId().toString() : null,
                        savedEntity.getFileUrl(),
                        BucketType.fromString(savedEntity.getBucketType()),
                        savedEntity.getDescription(),
                        savedEntity.getUploadedAt(),
                        savedEntity.getPreSignedUrlExpire(),
                        savedEntity.isArchived()));
    }


    @Override
    public boolean updatePreSignedUrlExpire(ObjectId mediaId, ObjectId ownerId, Instant newExpiration) {
        Query query = new Query(
                Criteria.where("_id").is(mediaId)
                        .and("ownerId").is(ownerId));
        Update update = new Update().set("preSignedUrlExpire", newExpiration);
        return mongoTemplate.updateFirst(query, update, MinioEntity.class).getModifiedCount() > 0;
    }

    @Override
    public boolean updateTypeId(ObjectId mediaId, ObjectId ownerId, @Nullable ObjectId newTypeId) {
        Query query = new Query(
                Criteria.where("_id").is(mediaId)
                        .and("ownerId").is(ownerId));
        Update update = new Update();

        if (newTypeId != null) {
            update.set("typeId", newTypeId);
        } else {
            update.unset("typeId"); // Allows removal of request association
        }

        return mongoTemplate.updateFirst(query, update, MinioEntity.class).getModifiedCount() > 0;
    }


    @Override
    public boolean archiveMedia(ObjectId mediaId, ObjectId ownerId) {
        Query query = new Query(
                Criteria.where("_id").is(mediaId)
                        .and("ownerId").is(ownerId));
        Update update = new Update().set("archived", true);
        return mongoTemplate.updateFirst(query, update, MinioEntity.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteMedia(ObjectId mediaId, ObjectId ownerId) {
        Query query = new Query(
                Criteria.where("_id").is(mediaId)
                        .and("ownerId").is(ownerId)
                        .and("archived").is(true)); // Prevent deletion of archived files

        return mongoTemplate.remove(query, MinioEntity.class).getDeletedCount() > 0;
    }
}
