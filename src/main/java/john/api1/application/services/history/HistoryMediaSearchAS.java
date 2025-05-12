package john.api1.application.services.history;

import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.components.exception.PersistenceHistoryException;
import john.api1.application.dto.mapper.history.media.MediaHistoryDTO;
import john.api1.application.dto.mapper.history.media.MediaHistoryPhotoDTO;
import john.api1.application.dto.mapper.history.media.MediaHistoryVideoDTO;
import john.api1.application.ports.repositories.request.IRequestCompletedSearchRepository;
import john.api1.application.ports.repositories.request.RequestCQRS;
import john.api1.application.ports.repositories.wrapper.MediaPreview;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.history.IHistoryMediaSearch;
import john.api1.application.ports.services.media.IMediaSearch;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoryMediaSearchAS implements IHistoryMediaSearch {
    private final IRequestCompletedSearchRepository completedSearch;
    private final IMediaSearch mediaSearch;
    private final IRequestSearch requestSearch;
    private final IPetOwnerSearch ownerSearch;
    private final IPetSearch petSearch;


    // Get request media
    // Get photo/video domain
    // Call minio to create temporary url
    // Return dto
    @Autowired
    public HistoryMediaSearchAS(IRequestCompletedSearchRepository completedSearch,
                                IMediaSearch mediaSearch,
                                IRequestSearch requestSearch,
                                IPetOwnerSearch ownerSearch,
                                IPetSearch petSearch) {
        this.completedSearch = completedSearch;
        this.mediaSearch = mediaSearch;
        this.requestSearch = requestSearch;
        this.ownerSearch = ownerSearch;
        this.petSearch = petSearch;
    }

    public Optional<MediaHistoryDTO> getRecentHistoryMedia() {
        try {
            var optionalRequest = requestSearch.searchRecentMediaRequest();
            if (optionalRequest.isEmpty()) return Optional.empty();

            RequestCQRS request = optionalRequest.get();
            String requestId = request.id();
            String ownerName = ownerSearch.getPetOwnerName(request.ownerId());
            String petName = petSearch.getPetName(request.petId());
            String description = request.description();

            switch (request.type()) {
                case PHOTO_REQUEST -> {
                    var photoRequest = completedSearch.findPhotoRequestByRequestId(requestId)
                            .orElseThrow(() -> new PersistenceHistoryException("No recent photo request history found"));

                    var media = mediaSearch.findByRequestId(photoRequest.requestId())
                            .orElseThrow(() -> new PersistenceException("No photos found. Failed to generate pre-signed URLs"));

                    var dto = new MediaHistoryPhotoDTO(
                            requestId,
                            MediaPreview.MediaPreviewDTO.map(media),
                            petName,
                            ownerName,
                            description,
                            request.createdAt(),
                            request.updatedAt()
                    );
                    return Optional.of(dto);
                }

                case VIDEO_REQUEST -> {
                    var videoRequest = completedSearch.findVideoRequestByRequestId(requestId)
                            .orElseThrow(() -> new PersistenceHistoryException("No recent video request history found"));


                    var video = mediaSearch.findByRequestId(videoRequest.requestId())
                            .filter(list -> list.size() == 1)
                            .orElseThrow(() -> new PersistenceException("No video found or multiple videos found. Failed to generate pre-signed URL"));

                    var firstUrl = video.getFirst().preSignedUrl();

                    var dto = new MediaHistoryVideoDTO(
                            requestId,
                            firstUrl,
                            petName,
                            ownerName,
                            description,
                            request.createdAt(),
                            request.updatedAt()
                    );
                    return Optional.of(dto);
                }

                default -> {
                    return Optional.empty(); // Future-proofing for other request types
                }
            }

        } catch (PersistenceException | DomainArgumentException | PersistenceHistoryException e) {
            throw new PersistenceHistoryException(e.getMessage());
        }
    }

}
