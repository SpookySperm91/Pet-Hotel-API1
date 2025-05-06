package john.api1.application.services.user;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.ports.repositories.owner.IPetOwnerCQRSRepository;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.services.IPetOwnerSearch;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetOwnerSearchAS implements IPetOwnerSearch {
    private final IPetOwnerCQRSRepository petOwnerCQRS;

    @Autowired
    public PetOwnerSearchAS(IPetOwnerCQRSRepository petOwnerCQRS) {
        this.petOwnerCQRS = petOwnerCQRS;
    }

    @Override
    public PetOwnerCQRS getPetOwnerBoardingDetails(String petOwnerId) {
        if (!ObjectId.isValid(petOwnerId)) throw new DomainArgumentException("Pet-owner id is invalid");

        return petOwnerCQRS.getDetails(petOwnerId)
                .orElseThrow(() -> new PersistenceException("Pet-owner cannot be found!"));
    }

    @Override
    public Optional<PetOwnerCQRS> getPetOwnerDetails(String petOwnerId) {
        if (!ObjectId.isValid(petOwnerId))
            throw new DomainArgumentException("Pet-owner id is invalid");

        return petOwnerCQRS.getDetails(petOwnerId)
                .or(() -> {
                    throw new PersistenceException("Pet-owner cannot be found!");
                });
    }

    @Override
    public List<String> getPetOwnerPets(String petOwnerId) {
        if (!ObjectId.isValid(petOwnerId)) throw new DomainArgumentException("Pet-owner id is invalid");

        return petOwnerCQRS.getAllPets(petOwnerId)
                .orElseThrow(() -> new PersistenceException("Pet-owner does not have pets!"));
    }

    @Override
    public Optional<String> verifyPetOwnership(String ownerId, String petId) {
        if (!ObjectId.isValid(ownerId) || !ObjectId.isValid(petId))
            throw new DomainArgumentException("Pet-owner or pet id cannot be mapped to ObjectId");

        return petOwnerCQRS.checkPetIfExist(ownerId, petId);
    }

    // Safe methods
    @Override
    public DomainResponse<PetOwnerCQRS> safeGetPetOwnerBoardingDetails(String petOwnerId) {
        if (!ObjectId.isValid(petOwnerId)) return DomainResponse.error("Pet-owner id is invalid");


        return petOwnerCQRS.getDetails(petOwnerId)
                .map(owner -> DomainResponse.success(owner, "Pet owner details retrieved"))
                .orElse(DomainResponse.error("Pet-owner not found"));
    }

    @Override
    public DomainResponse<String> safeVerifyPetOwnership(String ownerId, String petId) {
        if (!ObjectId.isValid(ownerId)) return DomainResponse.error("Pet-owner id is invalid");


        return petOwnerCQRS.checkPetIfExist(ownerId, petId)
                .map(owner -> DomainResponse.success(owner, "Pet owner pet's exist"))
                .orElse(DomainResponse.error("Pet-owner's pet cannot be found!"));
    }


    // Single fields
    @Override
    public String getPetOwnerName(String ownerId) {
        if (!ObjectId.isValid(ownerId)) throw new PersistenceException("Pet-owner id is invalid");

        var name = petOwnerCQRS.getPetOwnerName(ownerId);
        if (name.isEmpty()) throw new PersistenceException("Pet fileName cannot be found.");
        return name.get();

    }

    @Override
    public List<PetOwnerCQRS> getAllActivePetOwner() {
        var allActive = petOwnerCQRS.getAllActive();
        if (allActive.isEmpty()) throw new PersistenceException("Pet fileName cannot be found.");
        return allActive;
    }

    @Override
    public PetOwnerCQRS getRecentActivePetOwner() {
        var recent = petOwnerCQRS.getRecentActive();
        if (recent.isEmpty()) throw new PersistenceException("No recent active to be found.");
        return recent.get();
    }

    @Override
    public List<PetOwnerCQRS> getAllPendingPetOwner() {
        var allPending = petOwnerCQRS.getAllPending();
        if (allPending.isEmpty()) throw new PersistenceException("Pet fileName cannot be found.");
        return allPending;
    }

    @Override
    public PetOwnerCQRS getRecentPendingPetOwner() {
        var recent = petOwnerCQRS.getRecentPending();
        if (recent.isEmpty()) throw new PersistenceException("No recent active to be found.");
        return recent.get();
    }


}
