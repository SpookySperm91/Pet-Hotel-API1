package john.api1.application.services.media;

import john.api1.application.adapters.repositories.media.MinioSearchRepositoryMongo;
import john.api1.application.adapters.services.MinioAdapter;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.BucketType;
import john.api1.application.ports.repositories.records.MediaEntityPreview;
import john.api1.application.ports.repositories.records.MediaPreview;
import john.api1.application.ports.services.IMediaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MediaSearchAS implements IMediaSearch {
    private final MinioAdapter minioAdapter;
    private final MinioSearchRepositoryMongo mediaRepository;

    @Autowired
    public MediaSearchAS(MinioAdapter minioAdapter, MinioSearchRepositoryMongo mediaRepository) {
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

    private MediaPreview mapToMediaPreview(MediaEntityPreview entity) {
        String preSignedUrl = minioAdapter.getReadUrl(entity.bucketType(), entity.fileUrl());
        return new MediaPreview(
                entity.id(),
                entity.description(),
                entity.bucketType(),
                preSignedUrl,
                entity.uploadedAt()
        );
    }

    private <T> DomainResponse<List<T>> wrapListResponse(List<T> list, String emptyMessage) {
        return list.isEmpty() ? DomainResponse.error(emptyMessage) : DomainResponse.success(list);
    }
}
