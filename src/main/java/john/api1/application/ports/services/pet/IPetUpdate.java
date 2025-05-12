package john.api1.application.ports.services.pet;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.domain.models.PetDomain;
import john.api1.application.dto.mapper.pet.PetUpdatedDTO;
import john.api1.application.dto.request.PetRDTO;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.repositories.wrapper.MediaIdUrlExpire;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;
import john.api1.application.services.response.PetUpdateResponse;

public interface IPetUpdate {
    DomainResponse<String> updatePet(String petId, PetRDTO pet, String profilePicUrl);

    DomainResponse<PetUpdateResponse> updatePetName(String petId, String newPetName);

    DomainResponse<PetUpdateResponse> updatePetTypeAndBreed(String petId, String type, String breed);

    DomainResponse<PetUpdateResponse> updatePetProfilePicture(String petId, String profilePicUrl);

    DomainResponse<PetCQRS> updatePetStatusWithResponse(String petId, BoardingStatus status);

    DomainResponse<Void> updatePetStatus(String petId, BoardingStatus status);

    DomainResponse<PetUpdatedDTO> updatePet(PetDomain pet, String photoProfile);
}
