package john.api1.application.dto.mapper.request.search;

import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.history.ActivityLogDTO;
import john.api1.application.ports.repositories.wrapper.MediaIdUrlExpire;

import java.util.List;

public record RequestPhotoCompletedDTO(
        // id
        String id,  // Video Id
        String requestId,
        String ownerId,
        // photo
        List<MediaIdUrlExpire> photo,
        // details
        String requestType,
        String ownerName,
        String petName,
        String description,
        String adminResponse,
        String requestAt,  // String M, dd, yy at HH:mm:nn
        String completedAt  // String M, dd, yy at HH:mm:nn
) implements RequestSearchDTO, ActivityLogDTO {
    public static RequestPhotoCompletedDTO mapCompleted(PhotoRequestDomain domain,
                                                        List<MediaIdUrlExpire> media,
                                                        RequestDomain request,
                                                        String ownerName, String petName,
                                                        String requestAt, String completedAt) {
        return new RequestPhotoCompletedDTO(
                domain.id(), domain.requestId(), domain.ownerId(),
                media,
                request.getRequestType().getRequestType(), ownerName, petName,
                request.getDescription(), request.getResponseMessage(),
                requestAt, completedAt
        );
    }
}
