package john.api1.application.services.pet;

import john.api1.application.components.exception.PersistenceException;
import john.api1.application.ports.repositories.pet.IPetCQRSRepository;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.services.pet.IPetSearch;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetSearchAS implements IPetSearch {
    private final IPetCQRSRepository petSearch;

    @Autowired
    public PetSearchAS(IPetCQRSRepository petSearch) {
        this.petSearch = petSearch;
    }

    @Override
    public PetCQRS getPetBoardingDetails(String petId) {
        if (!ObjectId.isValid(petId)) throw new PersistenceException("Pet id is invalid.");

        var pet = petSearch.getPetDetails(petId);
        if (pet.isEmpty()) throw new PersistenceException("Pet cannot be found.");
        return pet.get();
    }

    @Override
    public PetCQRS getPetNameBreedSize(String petId) {
        if (!ObjectId.isValid(petId)) throw new PersistenceException("Pet id is invalid.");

        var name = petSearch.getPetNameBreedSize(petId);
        if (name.isEmpty()) throw new PersistenceException("Pet fileName cannot be found.");
        return name.get();
    }


    @Override
    public String getPetName(String petId) {
        if (!ObjectId.isValid(petId)) throw new PersistenceException("Pet id is invalid.");

        var name = petSearch.getPetName(petId);
        if (name.isEmpty()) throw new PersistenceException("Pet fileName cannot be found.");
        return name.get();
    }

}
