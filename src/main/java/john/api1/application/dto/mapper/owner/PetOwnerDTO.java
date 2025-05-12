package john.api1.application.dto.mapper.owner;

import java.time.Instant;
import java.util.List;

public record PetOwnerDTO(
        String id,
        String fullName,
        String email,
        String phoneNumber,
        String address,
        List<PetDTO> petDTOList,
        String customerSince,
        int currentlyBoarding
) {

    public record PetDTO(
            String id,
            String name,
            String animal,
            String breed,
            String size,
            String description,
            int age,
            boolean boarding,
            String photoId,
            String photoUrl,
            Instant expireAt
    ) {
    }
}
