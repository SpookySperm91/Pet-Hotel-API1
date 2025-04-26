package john.api1.application.dto.mapper.history.media;

import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.ports.repositories.wrapper.MediaPreview;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class MediaHistoryPhotoDTO implements MediaHistoryDTO {
    private final String requestId;
    private final List<MediaPreview.MediaPreviewDTO> photos;
    private final String requestType = RequestType.PHOTO_REQUEST.getRequestTypeToDto();
    private final String petName;
    private final String ownerName;
    private final String description;
    private final Instant requestOn;
    private final Instant completedOn;

    public MediaHistoryPhotoDTO(String requestId,
                                List<MediaPreview.MediaPreviewDTO> photos,
                                String petName,
                                String ownerName,
                                String description,
                                Instant requestOn,
                                Instant completedOn) {
        this.requestId = requestId;
        this.photos = photos;
        this.petName = petName;
        this.ownerName = ownerName;
        this.description = description;
        this.requestOn = requestOn;
        this.completedOn = completedOn;
    }
}
