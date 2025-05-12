package john.api1.application.services.pet;

import john.api1.application.components.exception.PersistenceException;
import john.api1.application.ports.repositories.pet.IPetCQRSRepository;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.repositories.pet.PetListCQRS;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.services.request.commit.CommitRequestMediasAS;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PetSearchAS implements IPetSearch {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CommitRequestMediasAS.class);
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
        if (name.isEmpty()) throw new PersistenceException("Pet cannot be found.");
        return name.get();
    }

    @Override
    public PetCQRS getRecent() {
        var recent = petSearch.getRecent();

        if(recent.isEmpty()) throw new PersistenceException("No recent pe can be found");
        return recent.get();
    }


    @Override
    public String getPetName(String petId) {
        if (!ObjectId.isValid(petId)) throw new PersistenceException("Pet id is invalid.");

        var name = petSearch.getPetName(petId);
        if (name.isEmpty()) {
            System.out.println("Pet name cannot be found. Pet-ID: " + petId);
            throw new PersistenceException("Pet fileName cannot be found.");
        }
        System.out.println("Pet name: " + name);
        return name.get();
    }

    // List
    @Override
    public List<PetCQRS> getAllByOwner(String ownerId) {
        if (!ObjectId.isValid(ownerId)) throw new PersistenceException("Owner id is invalid.");

        return petSearch.getAllByOwner(ownerId);
    }

    @Override
    public List<PetCQRS> getAll() {
        var pets = petSearch.getAll();
        if (pets.isEmpty()) return new ArrayList<>();

        return pets;
    }

    @Override
    public List<PetListCQRS> getAllByOwnerAsList(String id) {
        try {
            var pets = petSearch.getAllByOwnerAsList(id);
            if (pets.isEmpty()) return new ArrayList<>();
            return pets;

        } catch (PersistenceException e) {
            log.error("Error searching pets with per owner id, {}.", e.getMessage());
            return new ArrayList<>();
        } catch (Exception f) {
            log.error("Exception Error searching pets with per owner id, {}.", f.getMessage());
            return new ArrayList<>();

        }

    }
}
