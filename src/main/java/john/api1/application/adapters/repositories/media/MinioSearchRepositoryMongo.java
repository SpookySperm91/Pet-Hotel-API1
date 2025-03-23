package john.api1.application.adapters.repositories.media;

import john.api1.application.adapters.repositories.MinioEntity;
import john.api1.application.components.enums.BucketType;
import john.api1.application.ports.repositories.media.IMediaSearchRepository;
import john.api1.application.ports.repositories.records.MediaEntityPreview;
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

    private Optional<ObjectId> parseObjectId(String id) {
        return ObjectId.isValid(id) ? Optional.of(new ObjectId(id)) : Optional.empty();
    }

    private Query buildQueryForOwnerId(String ownerId) {
        return parseObjectId(ownerId)
                .map(id -> new Query(Criteria.where("ownerId").is(id)))
                .orElse(null);
    }

    private Query buildQueryForOwnerIdAndDate(String ownerId, Instant start, Instant end) {
        return parseObjectId(ownerId)
                .map(id -> new Query(
                        Criteria.where("ownerId").is(id)
                                .and("uploadedAt").gte(start).lte(end)
                ))
                .orElse(null);
    }

    @Override
    public Optional<MediaEntityPreview> findById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        MinioEntity entity = mongoTemplate.findOne(query, MinioEntity.class);
        return Optional.ofNullable(entity).map(MediaMapper::toMediaDomain);
    }

    @Override
    public List<MediaEntityPreview> findByOwnerIdAndUploadedBetween(String ownerId, Instant start, Instant end) {
        Query query = buildQueryForOwnerIdAndDate(ownerId, start, end);
        if (query == null) return List.of();
        return mongoTemplate.find(query, MinioEntity.class)
                .stream()
                .map(MediaMapper::toMediaDomain)
                .toList();
    }

    @Override
    public List<MediaEntityPreview> findByOwnerId(String ownerId) {
        Query query = buildQueryForOwnerId(ownerId);
        if (query == null) return List.of();
        return mongoTemplate.find(query, MinioEntity.class)
                .stream()
                .map(MediaMapper::toMediaDomain)
                .toList();
    }

    @Override
    public List<MediaEntityPreview> findByOwnerIdAndBucketTypeAndUploadedBetween(String ownerId, BucketType bucketType, Instant start, Instant end) {
        if (!ObjectId.isValid(ownerId)) return List.of();
        Query query = new Query(
                Criteria.where("ownerId").is(new ObjectId(ownerId))
                        .and("bucketType").is(bucketType)
                        .and("uploadedAt").gte(start).lte(end)
        );
        return mongoTemplate.find(query, MinioEntity.class)
                .stream()
                .map(MediaMapper::toMediaDomain)
                .toList();
    }

    @Override
    public List<MediaEntityPreview> findByTypeId(String typeId) {
        if (!ObjectId.isValid(typeId)) return List.of();
        Query query = new Query(Criteria.where("typeId").is(new ObjectId(typeId)));
        return mongoTemplate.find(query, MinioEntity.class)
                .stream()
                .map(MediaMapper::toMediaDomain)
                .toList();
    }
}

class MediaMapper {
    public static MediaEntityPreview toMediaDomain(MinioEntity entity) {
        return new MediaEntityPreview(
                entity.getId().toString(),
                entity.getDescription(),
                entity.getBucketType(),
                entity.getFileUrl(),
                entity.getUploadedAt()
        );
    }
}
