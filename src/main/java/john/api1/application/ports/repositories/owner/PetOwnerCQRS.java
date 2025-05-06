package john.api1.application.ports.repositories.owner;

import java.time.Instant;

public record PetOwnerCQRS(
        String id,
        String ownerName,
        String ownerEmail,
        String ownerPhoneNumber,
        String streetAddress,
        String cityAddress,
        String stateAddress,
        Instant createdAt
) {
}
