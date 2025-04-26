package john.api1.application.services.media;

import john.api1.application.adapters.services.MinioAdapter;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.BucketType;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.ports.repositories.media.IMediaSearchRepository;
import john.api1.application.ports.repositories.wrapper.MediaEntityPreview;
import john.api1.application.ports.repositories.wrapper.MediaIdUrlExpire;
import john.api1.application.ports.repositories.wrapper.MediaPreview;
import john.api1.application.ports.services.media.IMediaSearch;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class MediaSearchAS implements IMediaSearch {
    private final MinioAdapter minioAdapter;
    private final IMediaSearchRepository mediaRepository;

    @Autowired
    public MediaSearchAS(MinioAdapter minioAdapter, IMediaSearchRepository mediaRepository) {
        this.minioAdapter = minioAdapter;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public DomainResponse<MediaPreview> findById(String id) {
        var result = mediaRepository.findById(id)
                .map(this::mapToMediaPreview);
        return result.map(DomainResponse::success)
                .orElseGet(() -> DomainResponse.error("No media found for ID: " + id));
    }

    @Override
    public DomainResponse<List<MediaPreview>> findByOwnerIdAndUploadedBetween(String ownerId, Instant start, Instant end) {
        var results = mediaRepository.findByOwnerIdAndUploadedBetween(ownerId, start, end)
                .stream()
                .map(this::mapToMediaPreview)
                .toList();
        return wrapListResponse(results, "No media found for ownerId " + ownerId + " in the given period.");
    }

    @Override
    public DomainResponse<List<MediaPreview>> findByOwnerId(String ownerId) {
        var results = mediaRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::mapToMediaPreview)
                .toList();
        return wrapListResponse(results, "No media found for ownerId " + ownerId);
    }

    @Override
    public DomainResponse<List<MediaPreview>> findByOwnerIdAndBucketTypeAndUploadedBetween(String ownerId, BucketType bucketType, Instant start, Instant end) {
        var results = mediaRepository.findByOwnerIdAndBucketTypeAndUploadedBetween(ownerId, bucketType, start, end)
                .stream()
                .map(this::mapToMediaPreview)
                .toList();
        return wrapListResponse(results, "No media found for ownerId " + ownerId + " with bucketType " + bucketType);
    }

    @Override
    public DomainResponse<List<MediaPreview>> findByTypeId(String typeId) {
        var results = mediaRepository.findByTypeId(typeId)
                .stream()
                .map(this::mapToMediaPreview)
                .toList();
        return wrapListResponse(results, "No media found for typeId " + typeId);
    }

    @Override
    public Optional<MediaIdUrlExpire> findProfilePicByOwnerId(String ownerId) {
        if (!ObjectId.isValid(ownerId))
            throw new PersistenceException("Owner id for media retrieval cannot be converted to ObjectId");

        var results = mediaRepository.findProfilePicByOwnerId(ownerId);
        if (results.isEmpty()) return Optional.empty();

        String preSignedUrl = minioAdapter.getReadUrl(
                results.get().bucketType(),
                results.get().fileName());

        return Optional.of(new MediaIdUrlExpire(
                results.get().id(),
                preSignedUrl,
                results.get().expiredAt()
        ));
    }

    @Override
    public Optional<List<MediaPreview>> findByRequestId(String requestId) {
        if (!ObjectId.isValid(requestId))
            throw new PersistenceException("Invalid type-id from media search cannot be converted to ObjectId");

        var results = mediaRepository.findByTypeId(requestId)
                .stream()
                .map(this::mapToMediaPreview)
                .toList();

        return Optional.ofNullable(results);
    }


    private MediaPreview mapToMediaPreview(MediaEntityPreview entity) {
        String preSignedUrl = minioAdapter.getReadUrl(entity.bucketType(), entity.fileName());
        return new MediaPreview(
                entity.id(),
                entity.description(),
                entity.bucketType(),
                preSignedUrl,
                entity.expiredAt()
        );
    }

    private <T> DomainResponse<List<T>> wrapListResponse(List<T> list, String emptyMessage) {
        return list.isEmpty() ? DomainResponse.error(emptyMessage) : DomainResponse.success(list);
    }
}
