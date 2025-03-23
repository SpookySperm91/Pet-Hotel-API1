package john.api1.application.services.pet;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.BucketType;
import john.api1.application.domain.models.MediaDomain;
import john.api1.application.dto.mapper.ProfileResponseDTO;
import john.api1.application.ports.services.IMediaManagement;
import john.api1.application.ports.services.IPetProfilePhoto;
import john.api1.application.ports.services.IPetUpdate;
import org.springframework.stereotype.Service;

@Service
public class PetProfilePhotoAS implements IPetProfilePhoto {
    private final IPetUpdate petUpdate;
    private final IMediaManagement mediaManagement;

    public PetProfilePhotoAS(IPetUpdate petUpdate, IMediaManagement mediaManagement) {
        this.petUpdate = petUpdate;
        this.mediaManagement = mediaManagement;
    }

    @Override
    public DomainResponse<ProfileResponseDTO> processProfilePhoto(String id, String petName) {

        // Step 1: Create pre-signed url
        String fileName = mediaManagement.generateMediaObjectName(petName, id);
        var generateMedia = mediaManagement.generateMediaFile(id, fileName, BucketType.PROFILE_PHOTO);
        if (!generateMedia.isSuccess()) {
            return DomainResponse.error(generateMedia.getMessage(), DomainResponse.ErrorType.SERVER_ERROR);
        }

        // Step 2: Create media domain object and parse pre-signed url
        MediaDomain mediaDomain = MediaDomain.create(
                id,
                generateMedia.getData().preSignedUrl(),
                BucketType.PROFILE_PHOTO,
                generateMedia.getData().expiresAt()
        );

        // Step 3: Save domain object, update pet db. Check if successful or not
        var saveMediaResponse = mediaManagement.saveMediaFile(mediaDomain);
        if (!saveMediaResponse.isSuccess()) {
            return DomainResponse.error(saveMediaResponse.getMessage(), DomainResponse.ErrorType.SERVER_ERROR);
        }

        var updatePetResponse = petUpdate.updatePetProfilePicture(id, fileName);
        if (!updatePetResponse.isSuccess()) {
            return DomainResponse.error(updatePetResponse.getMessage(), DomainResponse.ErrorType.SERVER_ERROR);
        }

        // Step 4: Return Response
        return DomainResponse.success(
                new ProfileResponseDTO(
                        saveMediaResponse.getData().id(),
                        saveMediaResponse.getData().preSignedUrl(),
                        saveMediaResponse.getData().expiredAt()
                )
        );
    }

}
