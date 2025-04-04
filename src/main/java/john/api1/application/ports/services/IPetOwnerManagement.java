package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;

public interface IPetOwnerManagement {
    PetOwnerCQRS getPetOwnerBoardingDetails(String id);

    // safe wrapper
    DomainResponse<PetOwnerCQRS> safeGetPetOwnerBoardingDetails(String petOwnerId);
}
