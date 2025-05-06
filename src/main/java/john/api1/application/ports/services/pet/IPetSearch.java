package john.api1.application.ports.services.pet;

import john.api1.application.ports.repositories.pet.PetCQRS;

import java.util.List;

public interface IPetSearch {
    // Unsafe. Direct throw exception
    PetCQRS getPetBoardingDetails(String petId);
    PetCQRS getPetNameBreedSize(String petId);
    String getPetName(String petId);

    List<PetCQRS> getAllByOwner(String ownerId);
}
