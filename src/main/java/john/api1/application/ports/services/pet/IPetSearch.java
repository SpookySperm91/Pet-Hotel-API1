package john.api1.application.ports.services.pet;

import john.api1.application.ports.repositories.pet.PetCQRS;

public interface IPetSearch {
    // Unsafe. Direct throw exception
    PetCQRS getPetBoardingDetails(String petId);
}
