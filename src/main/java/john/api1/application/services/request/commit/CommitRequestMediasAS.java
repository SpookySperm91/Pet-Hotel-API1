package john.api1.application.services.request.commit;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.BucketType;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.RequestStatusDS;
import john.api1.application.domain.models.MediaDomain;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.domain.models.request.VideoRequestDomain;
import john.api1.application.dto.mapper.request.commit.RequestCompletedPhotoDTO;
import john.api1.application.dto.mapper.request.commit.RequestCompletedVideoDTO;
import john.api1.application.dto.request.request.admin.RequestCompletePhotoRDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteVideoRDTO;
import john.api1.application.ports.repositories.request.IRequestCompletedCreateRepository;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import john.api1.application.ports.services.media.IMediaManagement;
import john.api1.application.ports.services.request.IRequestSearch;
import john.api1.application.ports.services.request.IRequestUpdate;
import john.api1.application.ports.services.request.admin.ICommitRequestMedia;
import john.api1.application.services.aggregation.IAggregationCompletedRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = {DomainArgumentException.class, PersistenceException.class, MongoException.class})
public class CommitRequestMediasAS implements ICommitRequestMedia {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CommitRequestMediasAS.class);
    private final IRequestCompletedCreateRepository createRepository;
    private final IMediaManagement mediaManagement;
    private final IRequestUpdate requestUpdate;
    private final IBoardingSearch boardingSearch;
    private final IRequestSearch requestSearch;
    private final IAggregationCompletedRequest aggregation;

    @Autowired
    public CommitRequestMediasAS(IRequestCompletedCreateRepository createRepository,
                                 IMediaManagement mediaManagement,
                                 IRequestUpdate requestUpdate,
                                 IBoardingSearch boardingSearch,
                                 IRequestSearch requestSearch,
                                 IAggregationCompletedRequest aggregation) {
        this.createRepository = createRepository;
        this.mediaManagement = mediaManagement;
        this.requestUpdate = requestUpdate;
        this.boardingSearch = boardingSearch;
        this.requestSearch = requestSearch;
        this.aggregation = aggregation;
    }


    // Check request if valid and active
    // Generate URLs for photos to upload
    // Generate domains per photos
    // Save both to DB
    // Update request status as completed
    // Return aggregated DTO response
    @Override
    public DomainResponse<RequestCompletedPhotoDTO> commitPhotoRequest(RequestCompletePhotoRDTO request) {
        try {
            validateId(request.getRequestId());

            RequestDomain requestdomain = requestSearch.searchByRequestId(request.getRequestId());
            if (requestdomain.getRequestType() != RequestType.PHOTO_REQUEST) throw new DomainArgumentException("Invalid. The request is not a photo request");
            RequestStatusDS.isValidToCommit(requestdomain);

            // Generate media pre-sign url
            BoardingDomain boarding = validateActiveRequest(request.getRequestId());
            PreSignedUrlResponse[] mediaResponse = new PreSignedUrlResponse[request.getPhotos().size()];
            MediaDomain[] media = new MediaDomain[request.getPhotos().size()];

            for (int i = 0; i < mediaResponse.length; i++) {
                String fileName = mediaManagement.generateMediaObjectName(request.getPhotos().get(i).getName(), request.getRequestId());
                mediaResponse[i] = mediaManagement.unwrappedGenerateMediaFile(boarding.getOwnerId(), fileName, BucketType.REQUEST_PHOTO);
                media[i] = MediaDomain.create(
                        boarding.getOwnerId(),
                        request.getRequestId(),
                        fileName,
                        BucketType.REQUEST_PHOTO,
                        request.getNotes(),
                        mediaResponse[i].expiresAt()
                );
                // Save media to DB
                String id = mediaManagement.unwrappedSaveMediaFile(media[i]);
                media[i] = media[i].mapWithId(id);
            }

            var photo = PhotoRequestDomain.create(
                    request.getRequestId(),
                    boarding.getOwnerId(),
                    Arrays.stream(media)
                            .map(m -> new PhotoRequestDomain.MediaFile(m.id(), m.fileName()))
                            .collect(Collectors.toList()));

            // Save to DB
            // Update request status
            var saved = createRepository.createPhotoRequest(photo);
            var update = requestUpdate.markRequestAsCompletedWithMessage(request.getRequestId(), request.getNotes());
            String photoId = saved
                    .orElseThrow(() -> new PersistenceException("Failed to save video domain..."));
            if (!update.isSuccess())
                return DomainResponse.error(update.getMessage());

            // DTO
            photo = photo.mapWithId(photoId);
            var dto = aggregation.completedPhotoRequest(photo, Arrays.asList(mediaResponse));

            return DomainResponse.success(dto, "Photo request successfully completed");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    // Check request if valid and active
    // Generate URL for video to upload
    // Generate video domain
    // Save both to DB
    // Update request status as completed
    // Return aggregated DTO response
    @Override
    public DomainResponse<RequestCompletedVideoDTO> commitVideoRequest(RequestCompleteVideoRDTO request) {
        try {
            validateId(request.getRequestId());

            RequestDomain requestdomain = requestSearch.searchByRequestId(request.getRequestId());
            if (requestdomain.getRequestType() != RequestType.VIDEO_REQUEST) throw new DomainArgumentException("Invalid. The request is not a video request");
            RequestStatusDS.isValidToCommit(requestdomain);

            // Generate video media
            BoardingDomain boarding = validateActiveRequest(request.getRequestId());
            String fileName = mediaManagement.generateMediaObjectName(request.getVideoFile(), request.getRequestId());
            PreSignedUrlResponse mediaResponse = mediaManagement.unwrappedGenerateMediaFile(boarding.getOwnerId(), fileName, BucketType.REQUEST_VIDEO);
            MediaDomain media = MediaDomain.create(
                    boarding.getOwnerId(),
                    request.getRequestId(),
                    fileName,
                    BucketType.REQUEST_VIDEO,
                    request.getNotes(),
                    mediaResponse.expiresAt());

            // Save media
            String id = mediaManagement.unwrappedSaveMediaFile(media);
            media = media.mapWithId(id);

            // Created video domain
            var video = VideoRequestDomain.create(
                    request.getRequestId(),
                    boarding.getOwnerId(),
                    media.id(),
                    fileName);

            // Save to DB
            // Update request status
            var saved = createRepository.createVideoRequest(video);
            var update = requestUpdate.markRequestAsCompletedWithMessage(request.getRequestId(), request.getNotes());
            String videoId = saved
                    .orElseThrow(() -> new PersistenceException("Failed to save video domain..."));
            if (!update.isSuccess())
                throw new PersistenceException("Failed to update request as complete. Rollback all current changes");


            // DTO
            video = video.mapWithId(videoId);
            var dto = aggregation.completedVideoRequest(video, mediaResponse);

            return DomainResponse.success(dto, "Video request successfully completed");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    private BoardingDomain validateActiveRequest(String requestId) {
        validateId(requestId);

        var request = requestSearch.searchByRequestId(requestId);
        RequestStatusDS.isValidToCommit(request);

        var active = boardingSearch.findBoardingById(request.getBoardingId());
        if (!active.isSuccess())
            throw new DomainArgumentException(active.getMessage());
        if (!active.getData().isActive())
            throw new DomainArgumentException("Boarding of the request is inactive");

        return active.getData();
    }


    private void validateId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid id cannot be converted to ObjectId");
    }


}
