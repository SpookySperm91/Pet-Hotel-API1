package john.api1.application.dto.mapper.owner;

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
            int age,
            boolean boarding
    ) {
    }
}
