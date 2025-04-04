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

@Service
public class PetOwnerSearchAS implements IPetOwnerManagement {
    private final IPetOwnerCQRSRepository petOwnerCQRS;

    @Autowired
    public PetOwnerSearchAS(IPetOwnerCQRSRepository petOwnerCQRS) {
        this.petOwnerCQRS = petOwnerCQRS;
    }

    public PetOwnerCQRS getPetOwnerBoardingDetails(String petOwnerId) {
        if (!ObjectId.isValid(petOwnerId)) throw new DomainArgumentException("Pet-owner id is invalid");

        return petOwnerCQRS.getDetails(petOwnerId)
                .orElseThrow(() -> new PersistenceException("Pet-owner cannot be found!"));

    }

    public DomainResponse<PetOwnerCQRS> safeGetPetOwnerBoardingDetails(String petOwnerId) {
        if (!ObjectId.isValid(petOwnerId)) {
            return DomainResponse.error("Pet-owner ID is invalid");
        }

        return petOwnerCQRS.getDetails(petOwnerId)
                .map(owner -> DomainResponse.success(owner, "Pet owner details retrieved"))
                .orElse(DomainResponse.error("Pet-owner not found"));
    }

}
