package john.api1.application.ports.repositories.request;

import john.api1.application.components.enums.boarding.RequestType;

import java.time.Instant;

public record RequestCQRS(
        String id,
        String ownerId,
        String petId,
        String boardingId,
        RequestType type,
        Instant createdAt,
        Instant updatedAt,
        Boolean active
) {
    public static RequestCQRS mapIds(String id, String ownerId, String petId, String boardingId) {
        return new RequestCQRS(id, ownerId, petId, boardingId, null, null, null,null);
    }

}
