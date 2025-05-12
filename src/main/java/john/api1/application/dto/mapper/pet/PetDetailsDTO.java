package john.api1.application.dto.mapper.pet;

import john.api1.application.dto.mapper.request.search.PetRequestHistoryDTO;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.repositories.wrapper.MediaIdUrlExpire;

import java.time.Instant;
import java.util.List;

public record PetDetailsDTO(
        String id,
        String name,
        String animal,
        String breed,
        String size,
        String description,
        int age,
        boolean boarding,

        // Photo
        String photoId,
        String photoUrl,
        Instant expireAt,

        // Owner
        String ownerId,
        String ownerName,

        // List
        List<PetBoardingHistoryDTO> boardingHistory,
        List<PetRequestHistoryDTO> requestHistory
) {
    public static PetDetailsDTO map(PetCQRS pet, MediaIdUrlExpire media,
                                    String ownerId, String ownerName,
                                    List<PetBoardingHistoryDTO> boarding, List<PetRequestHistoryDTO> request) {
        return new PetDetailsDTO(
                pet.id(), pet.petName(), pet.animalType(), pet.breed(), pet.size(), pet.specialDescription(), pet.age(), pet.boarding(),
                media.id(), media.mediaUrl(), media.expireAt(),
                ownerId, ownerName,
                boarding, request
        );
    }

}
