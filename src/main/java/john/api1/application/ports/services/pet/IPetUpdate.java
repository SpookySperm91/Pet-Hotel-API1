package john.api1.application.ports.services.pet;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.PetRDTO;
import john.api1.application.services.response.PetUpdateResponse;

public interface IPetUpdate {
    DomainResponse<String> updatePet(String petId, PetRDTO pet, String profilePicUrl);
    DomainResponse<PetUpdateResponse> updatePetName(String petId, String newPetName);
    DomainResponse<PetUpdateResponse> updatePetTypeAndBreed(String petId, String type, String breed);
    DomainResponse<PetUpdateResponse> updatePetProfilePicture(String petId, String profilePicUrl);
}
