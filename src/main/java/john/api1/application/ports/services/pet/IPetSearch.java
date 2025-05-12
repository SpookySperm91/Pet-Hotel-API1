package john.api1.application.ports.services.pet;

import john.api1.application.domain.models.PetDomain;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.repositories.pet.PetListCQRS;

import java.util.List;

public interface IPetSearch {
    // Unsafe. Direct throw exception
    PetCQRS getPetBoardingDetails(String petId);
    PetCQRS getPetNameBreedSize(String petId);
    PetCQRS getRecent();
    String getPetName(String petId);

    List<PetCQRS> getAllByOwner(String ownerId);
    List<PetCQRS> getAll();


    List<PetListCQRS> getAllByOwnerAsList(String id);

}
