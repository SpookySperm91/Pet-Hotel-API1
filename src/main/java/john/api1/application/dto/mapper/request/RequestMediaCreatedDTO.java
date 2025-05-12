package john.api1.application.dto.mapper.request;


import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.history.ActivityLogDTO;
import john.api1.application.dto.mapper.request.search.RequestSearchDTO;

import java.time.Instant;

public record RequestMediaCreatedDTO(
        // id
        String id,
        String ownerId,
        String petId,
        String boardingId,
        // information
        String ownerName,
        String petName,
        String requestType,
        String requestStatus,
        String description,
        String adminResponse,
        Instant requestAt
) implements RequestSearchDTO, ActivityLogDTO {
    public static RequestMediaCreatedDTO map(RequestDomain domain, String ownerName, String petName) {
        return new RequestMediaCreatedDTO(
                domain.getId(), domain.getOwnerId(), domain.getPetId(), domain.getBoardingId(),
                ownerName, petName,
                domain.getRequestType().getRequestType(), domain.getRequestStatus().getRequestStatus(),
                domain.getDescription(), domain.getResponseMessage(), domain.getRequestTime()
        );
    }


}

