package john.api1.application.dto.mapper.request;

import jakarta.annotation.Nullable;
import john.api1.application.components.DateUtils;
import john.api1.application.domain.models.request.GroomingDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.request.search.RequestSearchDTO;
import john.api1.application.ports.repositories.request.GroomingCQRS;

public record RequestGroomingCreatedDTO(
        // id
        String id,
        String ownerId,
        String petId,
        String boardingId,
        // information
        String ownerName,
        String petName,
        String petSize,
        String requestType,
        // pricing
        double price,
        String groomingType,
        //
        String requestStatus,
        @Nullable
        String description,
        @Nullable
        String adminResponse,
        String requestAt
) implements RequestSearchDTO {
    public static RequestGroomingCreatedDTO map(RequestDomain domain, GroomingCQRS grooming, String ownerName, String petName, String petSize) {
        return new RequestGroomingCreatedDTO(
                domain.getId(), domain.getOwnerId(), domain.getPetId(), domain.getBoardingId(),
                ownerName, petName, petSize, domain.getRequestType().getRequestType(),
                grooming.price(), grooming.groomingType().getGroomingTypeToDTO(),
                domain.getRequestStatus().getRequestStatus(),
                domain.getDescription(), domain.getResponseMessage(), DateUtils.formatInstantWithTime(domain.getRequestTime())
        );
    }

    public static RequestGroomingCreatedDTO map(RequestDomain domain, GroomingDomain grooming, String ownerName, String petName, String petSize) {
        return new RequestGroomingCreatedDTO(
                domain.getId(), domain.getOwnerId(), domain.getPetId(), domain.getBoardingId(),
                ownerName, petName, petSize, domain.getRequestType().getRequestType(),
                grooming.getGroomingPrice(), grooming.getGroomingType().getGroomingTypeToDTO(),
                domain.getRequestStatus().getRequestStatus(), domain.getDescription(), domain.getResponseMessage(),  DateUtils.formatInstantWithTime(domain.getRequestTime())
        );
    }
}
