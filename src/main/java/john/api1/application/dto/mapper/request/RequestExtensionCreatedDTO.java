package john.api1.application.dto.mapper.request;

import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.RequestDomain;

import java.time.Instant;

public record RequestExtensionCreatedDTO(
        // id
        String id,
        String ownerId,
        String petId,
        String boardingId,
        // information
        String ownerName,
        String petName,
        String requestType,
        // duration
        long duration,
        String unit,
        //
        String requestStatus,
        String description,
        Instant requestAt
) {
    public static RequestExtensionCreatedDTO map(RequestDomain domain, ExtensionDomain extension, String ownerName, String petName, String unit) {
        return new RequestExtensionCreatedDTO(
                domain.getId(), domain.getOwnerId(), domain.getPetId(), extension.getBoardingId(),
                ownerName, petName, domain.getRequestType().getRequestType(),
                extension.getExtendedHours(), unit,
                domain.getRequestStatus().getRequestStatus(), domain.getDescription(), domain.getRequestTime()
        );
    }
}
