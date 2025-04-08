package john.api1.application.dto.mapper.request;


import john.api1.application.domain.models.request.RequestDomain;

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
        Instant requestAt
) {

    public static RequestMediaCreatedDTO map(RequestDomain domain, String ownerName, String petName) {
        return new RequestMediaCreatedDTO(
                domain.getId(), domain.getOwnerId(), domain.getPetId(), domain.getBoardingId(),
                ownerName, petName,
                domain.getRequestType().getRequestType(), domain.getRequestStatus().getRequestStatus(),
                domain.getDescription(), domain.getRequestTime()
        );
    }
}

