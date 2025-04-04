package john.api1.application.services.pet;

import john.api1.application.components.exception.DomainArgumentException;
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
        if (!ObjectId.isValid(petId)) throw new DomainArgumentException("Pet id is invalid.");

        return petSearch.getPetDetails(petId)
                .orElseThrow(()-> new PersistenceException("Pet cannot be found."));

    }
}
