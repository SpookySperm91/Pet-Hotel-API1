package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.domain.models.PetDomain;
import john.api1.application.dto.request.PetRequestDTO;
import john.api1.application.services.response.PetUpdateResponse;

public interface IPetUpdate {
    DomainResponse<String> updatePet(String petId, PetRequestDTO pet, String profilePicUrl);
    DomainResponse<PetUpdateResponse> updatePetName(String petId, String newPetName);
    DomainResponse<PetUpdateResponse> updatePetTypeAndBreed(String petId, String type, String breed);
    DomainResponse<PetUpdateResponse> updatePetProfilePicture(String petId, String profilePicUrl);
}
