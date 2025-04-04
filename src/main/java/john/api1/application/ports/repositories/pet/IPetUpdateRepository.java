package john.api1.application.ports.repositories.pet;

import john.api1.application.domain.models.PetDomain;

public interface IPetUpdateRepository {
    boolean updatePet(PetDomain pet);
    boolean updatePetName(String petId, String newPetName);
    boolean updatePetTypeAndBreed(String petId, String type, String breed);
    boolean updatePetProfilePicture(String petId, String profilePicUrl);
    boolean updatePetStatus(String petId, boolean status);
}
