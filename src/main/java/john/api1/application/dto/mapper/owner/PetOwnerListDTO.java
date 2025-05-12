package john.api1.application.dto.mapper.owner;

import jakarta.annotation.Nullable;

import java.util.List;

public record PetOwnerListDTO(
        String id,
        String fullName,
        String email,
        String phoneNumber,
        String address,
        List<PetListDTO> pets

) {
    public record PetListDTO(
            @Nullable String petId,
            @Nullable String petName,
            @Nullable String animalType,
            @Nullable Boolean boarding
    ) {
    }

}
