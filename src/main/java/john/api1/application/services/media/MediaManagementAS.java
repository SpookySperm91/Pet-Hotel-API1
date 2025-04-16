package john.api1.application.services.media;

import com.mongodb.MongoException;
import john.api1.application.adapters.repositories.media.MinioCreateUpdateRepository;
import john.api1.application.adapters.services.MinioAdapter;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.BucketType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.MediaDomain;
import john.api1.application.ports.repositories.wrapper.MediaPreview;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;
import john.api1.application.ports.services.media.IMediaManagement;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Transactional(rollbackFor = {DomainArgumentException.class, PersistenceException.class, MongoException.class})
public class MediaManagementAS implements IMediaManagement {
    private final MinioAdapter minioAdapter;
    private final MinioCreateUpdateRepository mediaRepository;

    @Autowired
    public MediaManagementAS(MinioAdapter minioAdapter, MinioCreateUpdateRepository mediaRepository) {
        this.minioAdapter = minioAdapter;
        this.mediaRepository = mediaRepository;
    }


    public String generateMediaObjectName(String name, String id) {
        return name + ":" + id;
    }

    public DomainResponse<PreSignedUrlResponse> generateMediaFile(String ownerId, String fileName, BucketType bucketType) {
        if (!ObjectId.isValid(ownerId)) return DomainResponse.error("Invalid ownerId");
        if (fileName == null || fileName.isBlank()) return DomainResponse.error("Filename cannot be empty");

        try {
            String newUrl = minioAdapter.getUploadUrl(bucketType, fileName);
            Instant expirationTime = Instant.now().plus(bucketType.getMinuteExpire(), ChronoUnit.MINUTES);
            return DomainResponse.success(new PreSignedUrlResponse(newUrl, expirationTime));
        } catch (Exception e) {
            return DomainResponse.error("Something wrong. Try again");
        }
    }

    public DomainResponse<MediaPreview> saveMediaFile(MediaDomain media) {
        if (!ObjectId.isValid(media.ownerId()) ||
                media.typeId() != null && !ObjectId.isValid(media.typeId())) {    // if not null, check if valid
            return DomainResponse.error("Invalid ownerId or typeId.");
        }

        try {
            var savedMedia = mediaRepository.save(media);
            if (savedMedia.isPresent()) {
                MediaDomain mediaDomain = savedMedia.get();
                return DomainResponse.success(
                        new MediaPreview(
                                mediaDomain.id(),
                                mediaDomain.description(),
                                mediaDomain.bucketType(),
                                mediaDomain.fileName(),
                                mediaDomain.preSignedUrlExpire()
                        )
                );
            }
            return DomainResponse.error("Failed to save media to database.");
        } catch (RuntimeException e) {
            return DomainResponse.error("Database error: " + e.getMessage());
        }
    }

    public DomainResponse<MediaPreview> updatePreSignedUrlExpire(String mediaId, String ownerId) {
        return DomainResponse.error("");
    }


    public DomainResponse<MediaPreview> updateTypeId(String mediaId, String ownerId, ObjectId newTypeId) {
        return DomainResponse.error("");
    }

    public DomainResponse<String> archiveMedia(String mediaId, String ownerId) {
        return DomainResponse.error("");
    }

    public DomainResponse<String> deleteMedia(String mediaId, String ownerId) {
        return DomainResponse.error("");
    }


    public void deleteMediasByRequest(String requestId) {
        if (!ObjectId.isValid(requestId))
            throw new PersistenceException("Invalid request id cannot be convert to ObjectId");

        var delete = mediaRepository.deleteMediaByRequest(requestId);
        if(!delete) throw new PersistenceException("Failed to delete medias of this request: " + requestId);

    }

    // Unsafe
    public PreSignedUrlResponse unwrappedGenerateMediaFile(String ownerId, String fileName, BucketType bucketType) {
        if (!ObjectId.isValid(ownerId)) throw new PersistenceException("Invalid owner id");
        if (fileName == null || fileName.isBlank()) throw new PersistenceException("Filename cannot be empty");

        try {
            String newUrl = minioAdapter.getUploadUrl(bucketType, fileName);
            Instant expirationTime = Instant.now().plus(bucketType.getMinuteExpire(), ChronoUnit.MINUTES);
            return new PreSignedUrlResponse(newUrl, expirationTime);
        } catch (Exception e) {
            throw new PersistenceException("Something wrong. Try again");
        }
    }

    public String unwrappedSaveMediaFile(MediaDomain media) {
        if (!ObjectId.isValid(media.ownerId()) ||
                media.typeId() != null && !ObjectId.isValid(media.typeId())) {    // if not null, check if valid
            throw new PersistenceException("Invalid ownerId or typeId.");
        }

        try {
            var savedMedia = mediaRepository.save(media);
            if (savedMedia.isPresent()) {
                return savedMedia.get().id().toString();
            }

            throw new PersistenceException("Failed to save media to database.");
        } catch (RuntimeException e) {
            throw new PersistenceException("Failed to save media to database.");
        }
    }


}
