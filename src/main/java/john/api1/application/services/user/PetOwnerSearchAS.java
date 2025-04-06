package john.api1.application.services.user;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.ports.repositories.owner.IPetOwnerCQRSRepository;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.services.IPetOwnerManagement;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetOwnerSearchAS implements IPetOwnerManagement {
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
        if (!ObjectId.isValid(petOwnerId)) return DomainResponse.error("Pet-owner ID is invalid");


        return petOwnerCQRS.getDetails(petOwnerId)
                .map(owner -> DomainResponse.success(owner, "Pet owner details retrieved"))
                .orElse(DomainResponse.error("Pet-owner not found"));
    }

    @Override
    public DomainResponse<String> safeVerifyPetOwnership(String ownerId, String petId){
        if (!ObjectId.isValid(ownerId)) return DomainResponse.error("Pet-owner ID is invalid");


        return petOwnerCQRS.checkPetIfExist(ownerId, petId)
                .map(owner -> DomainResponse.success(owner, "Pet owner pet's exist"))
                .orElse(DomainResponse.error("Pet-owner's pet cannot be found!"));
    }
}
