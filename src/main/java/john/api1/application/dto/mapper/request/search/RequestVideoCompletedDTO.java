package john.api1.application.dto.mapper.request.search;

import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.domain.models.request.VideoRequestDomain;
import john.api1.application.dto.mapper.history.ActivityLogDTO;
import john.api1.application.ports.repositories.wrapper.MediaIdUrlExpire;

import java.time.Instant;

public record RequestVideoCompletedDTO(
        // id
        String id,  // Video Id
        String requestId,
        String ownerId,
        // video
        String mediaId,
        String url,
        Instant expiredAt,
        // details
        String requestType,
        String ownerName,
        String petName,
        String description,
        String adminResponse,
        String requestAt,  // String M, dd, yy at HH:mm:nn
        String completedAt  // String M, dd, yy at HH:mm:nn
) implements RequestSearchDTO, ActivityLogDTO {
    public static RequestVideoCompletedDTO mapCompleted(VideoRequestDomain domain,
                                                        MediaIdUrlExpire media,
                                                        RequestDomain request,
                                                        String ownerName, String petName,
                                                        String requestAt, String completedAt) {
        return new RequestVideoCompletedDTO(
                domain.id(), domain.requestId(), domain.ownerId(),
                media.id(), media.mediaUrl(), media.expireAt(),
                request.getRequestType().getRequestType(), ownerName, petName,
                request.getDescription(), request.getResponseMessage(),
                requestAt, completedAt
        );
    }
}
