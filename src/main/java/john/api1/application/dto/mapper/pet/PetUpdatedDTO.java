package john.api1.application.dto.mapper.pet;

import jakarta.annotation.Nullable;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;

public record PetUpdatedDTO(
        String petId,
        String ownerId,
        String petName,
        String breed,
        String size,
        int age,
        @Nullable
        PreSignedUrlResponse url
) {
}
