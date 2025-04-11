package john.api1.application.services.request.commit;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.BucketType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.MediaDomain;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.dto.request.request.admin.RequestCompletePhotoRDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteVideoRDTO;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;
import john.api1.application.ports.services.IPetOwnerManagement;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import john.api1.application.ports.services.media.IMediaManagement;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestSearch;
import john.api1.application.ports.services.request.IRequestUpdate;
import john.api1.application.ports.services.request.admin.ICommitRequestMedia;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;


@Service
public class CommitRequestMediasAS implements ICommitRequestMedia {
    private final IMediaManagement mediaManagement;
    private final IBoardingSearch boardingSearch;
    private final IRequestSearch requestSearch;
    private final IRequestUpdate requestUpdate;
    private final IPetOwnerManagement ownerSearch;
    private final IPetSearch petSearch;

    @Autowired
    public CommitRequestMediasAS(IMediaManagement mediaManagement,
                                 IBoardingSearch boardingSearch,
                                 IRequestSearch requestSearch,
                                 IRequestUpdate requestUpdate,
                                 IPetOwnerManagement ownerSearch,
                                 IPetSearch petSearch) {
        this.mediaManagement = mediaManagement;
        this.boardingSearch = boardingSearch;
        this.requestSearch = requestSearch;
        this.requestUpdate = requestUpdate;
        this.ownerSearch = ownerSearch;
        this.petSearch = petSearch;
    }


    // Check request if valid and active
    // Generate domains per photos
    // Save to DB
    // Generate URLs for to upload
    //
    @Override
    public DomainResponse<Void> commitPhotoRequest(RequestCompletePhotoRDTO request) {
        try {
            validateId(request.getRequestId());
            var update = requestSearch.searchById(request.getRequestId());
            var active = boardingSearch.findBoardingById(update.getBoardingId());
            if (!active.isSuccess()) return DomainResponse.error(active.getMessage());
            if (!active.getData().isActive()) return DomainResponse.error("Boarding of the request is inactive");

            // Generate media pre-sign url
            BoardingDomain boarding = active.getData();
            PreSignedUrlResponse[] mediaResponse = new PreSignedUrlResponse[request.getPhotos().size()];
            MediaDomain[] media = new MediaDomain[request.getPhotos().size()];

            for (int i = 0; i < mediaResponse.length; i++) {
                String fileName = mediaManagement.generateMediaObjectName(request.getPhotos().get(i).getName(), request.getRequestId());
                mediaResponse[i] = mediaManagement.unwrappedGenerateMediaFile(boarding.getOwnerId(), fileName, BucketType.REQUEST_PHOTO);
                media[i] = MediaDomain.create(
                        boarding.getOwnerId(),
                        request.getRequestId(),
                        fileName,
                        BucketType.PROFILE_PHOTO,
                        request.getNotes(),
                        mediaResponse[i].expiresAt()
                );
                // Save media to DB
                mediaManagement.saveMediaFile(media[i]);
            }

            var domain = PhotoRequestDomain.create(
                    request.getRequestId(),
                    boarding.getOwnerId(),
                    Arrays.stream(media)
                            .map(m -> new PhotoRequestDomain.MediaFile(m.id(), m.fileName()))
                            .collect(Collectors.toList()));

            return DomainResponse.success();
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

//    PhotoRequestDomain r;  // After successfully created
//    RequestDomain f;  // Pending request to commit
//    MediaDomain m;  // Original domain of media


    @Override
    public DomainResponse<Void> commitVideoRequest(RequestCompleteVideoRDTO request) {
        return DomainResponse.success();
    }


    private void validateId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid id cannot be converted to ObjectId");
    }
}
