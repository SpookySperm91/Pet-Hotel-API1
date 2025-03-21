package john.api1.application.adapters.repositories.media;

import john.api1.application.adapters.repositories.MinioEntity;
import john.api1.application.components.enums.BucketType;
import john.api1.application.ports.repositories.media.IMediaSearchRepository;
import john.api1.application.ports.repositories.records.MediaPreview;
import john.api1.application.ports.repositories.records.MediaUrlAndId;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class MinioSearchRepositoryMongo implements IMediaSearchRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public MinioSearchRepositoryMongo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<MediaPreview> findById(String id) {
        if (!ObjectId.isValid(id)) return Optional.empty();

        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        MinioEntity entity = mongoTemplate.findOne(query, MinioEntity.class);

        return Optional.ofNullable(entity).map(MediaMapper::toMediaPreview);
    }

    @Override
    public List<MediaPreview> findByOwnerIdAndUploadedBetween(String ownerId, Instant start, Instant end) {
        if (!ObjectId.isValid(ownerId)) return List.of();

        Query query = new Query(
                Criteria.where("ownerId").is(new ObjectId(ownerId))
                        .and("uploadedAt").gte(start).lte(end)
        );

        return mongoTemplate.find(query, MinioEntity.class)
                .stream()
                .map(MediaMapper::toMediaPreview)
                .toList();
    }

    @Override
    public List<MediaPreview> findByOwnerId(String ownerId) {
        if (!ObjectId.isValid(ownerId)) return List.of();

        Query query = new Query(Criteria.where("ownerId").is(new ObjectId(ownerId)));

        return mongoTemplate.find(query, MinioEntity.class)
                .stream()
                .map(MediaMapper::toMediaPreview)
                .toList();
    }

    @Override
    public List<MediaPreview> findByOwnerIdAndBucketTypeAndUploadedBetween(String ownerId, BucketType bucketType, Instant start, Instant end) {
        if (!ObjectId.isValid(ownerId)) return List.of();

        Query query = new Query(
                Criteria.where("ownerId").is(new ObjectId(ownerId))
                        .and("bucketType").is(bucketType)
                        .and("uploadedAt").gte(start).lte(end)
        );

        return mongoTemplate.find(query, MinioEntity.class)
                .stream()
                .map(MediaMapper::toMediaPreview)
                .toList();
    }

    @Override
    public List<MediaPreview> findByTypeId(String typeId) {
        if (!ObjectId.isValid(typeId)) return List.of();

        Query query = new Query(Criteria.where("typeId").is(new ObjectId(typeId)));

        return mongoTemplate.find(query, MinioEntity.class)
                .stream()
                .map(MediaMapper::toMediaPreview)
                .toList();
    }
}

class MediaMapper {
    public static MediaPreview toMediaPreview(MinioEntity entity) {
        return new MediaPreview(
                entity.getId().toString(),
                entity.getDescription(),
                entity.getBucketType(),
                entity.getFileUrl(),
                entity.getUploadedAt()
        );
    }

    public static MediaUrlAndId toMediaUrlAndId(MinioEntity entity) {
        return new MediaUrlAndId(
                entity.getId().toString(),
                entity.getFileUrl()
        );
    }
}
