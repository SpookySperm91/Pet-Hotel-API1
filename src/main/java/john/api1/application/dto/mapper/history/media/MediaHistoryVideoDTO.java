package john.api1.application.dto.mapper.history.media;

import john.api1.application.components.enums.boarding.RequestType;
import lombok.Getter;

import java.time.Instant;

@Getter
public class MediaHistoryVideoDTO implements MediaHistoryDTO {
    private final String requestId;
    private final String videoUrl;
    private final String requestType = RequestType.VIDEO_REQUEST.getRequestTypeToDto();
    private final String petName;
    private final String ownerName;
    private final String description;
    private final Instant requestOn;
    private final Instant completedOn;

    public MediaHistoryVideoDTO(String requestId,
                                String videoUrl,
                                String petName,
                                String ownerName,
                                String description,
                                Instant requestOn,
                                Instant completedOn) {
        this.requestId = requestId;
        this.videoUrl = videoUrl;
        this.petName = petName;
        this.ownerName = ownerName;
        this.description = description;
        this.requestOn = requestOn;
        this.completedOn = completedOn;
    }
}
