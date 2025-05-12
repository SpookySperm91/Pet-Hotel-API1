package john.api1.application.dto.mapper.request;

import jakarta.annotation.Nullable;
import john.api1.application.components.DateUtils;
import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.request.search.RequestSearchDTO;
import john.api1.application.ports.repositories.request.ExtensionCQRS;

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
//        String previousEnd,
//        String newEnd,
        //
        String requestStatus,
        @Nullable
        String description,
        @Nullable
        String adminResponse,
        String requestAt
) implements RequestSearchDTO {

    public static RequestExtensionCreatedDTO map(RequestDomain domain, ExtensionDomain extension, String ownerName, String petName, String unit) {
        return new RequestExtensionCreatedDTO(
                domain.getId(), domain.getOwnerId(), domain.getPetId(), extension.getBoardingId(),
                ownerName, petName, domain.getRequestType().getRequestType(),
                extension.getExtendedHours(), unit,
                domain.getRequestStatus().getRequestStatus(), domain.getDescription(), domain.getResponseMessage(), DateUtils.formatInstantWithTime(domain.getRequestTime())
        );
    }


    public static RequestExtensionCreatedDTO searchMap(RequestDomain domain, ExtensionCQRS extension, String ownerName, String petName) {
        String unit = switch (extension.durationType()) {
            case DAYCARE -> "Hours";
            case LONG_STAY -> "Days";
        };
        return new RequestExtensionCreatedDTO(
                domain.getId(), domain.getOwnerId(), domain.getPetId(), extension.boardingId(),
                ownerName, petName, domain.getRequestType().getRequestType(),
                extension.extendedHours(), unit,
                domain.getRequestStatus().getRequestStatus(), domain.getDescription(), domain.getResponseMessage(), DateUtils.formatInstantWithTime(domain.getRequestTime())
        );
    }
}
