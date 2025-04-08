package john.api1.application.dto.mapper.request;

import john.api1.application.components.enums.boarding.RequestType;

import java.math.BigDecimal;
import java.time.Instant;

public record RequestServiceCreatedDTO (
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
        BigDecimal price,
        //
        String requestStatus,
        String description,
        Instant requestAt
){
}
