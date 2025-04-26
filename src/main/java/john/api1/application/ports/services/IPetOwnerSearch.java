package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;

import java.util.List;
import java.util.Optional;

public interface IPetOwnerSearch {
    PetOwnerCQRS getPetOwnerBoardingDetails(String id);
    Optional<PetOwnerCQRS> getPetOwnerDetails(String id);

    List<String> getPetOwnerPets(String id);

    Optional<String> verifyPetOwnership(String ownerId, String petId);

    // safe wrapper
    DomainResponse<PetOwnerCQRS> safeGetPetOwnerBoardingDetails(String petOwnerId);
    DomainResponse<String> safeVerifyPetOwnership(String ownerId, String petId);

    // Single fields
    String getPetOwnerName(String ownerId);
}
