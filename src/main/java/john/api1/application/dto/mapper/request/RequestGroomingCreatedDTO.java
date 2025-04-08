package john.api1.application.dto.mapper.request;

import john.api1.application.domain.models.request.RequestDomain;

import java.time.Instant;

public record RequestGroomingCreatedDTO(
        // id
        String id,
        String ownerId,
        String petId,
        String boardingId,
        // information
        String ownerName,
        String petName,
        String requestType,
        // pricing
        double price,
        String size,
        //
        String requestStatus,
        String description,
        Instant requestAt
) {

    public static RequestGroomingCreatedDTO map(RequestDomain domain, String ownerName, String petName, double price, String size) {
        return new RequestGroomingCreatedDTO(
                domain.getId(), domain.getOwnerId(), domain.getPetId(), domain.getBoardingId(),
                ownerName, petName, domain.getRequestType().getRequestType(),
                price, size,
                domain.getRequestStatus().getRequestStatus(), domain.getDescription(), domain.getRequestTime()
        );
    }
}
