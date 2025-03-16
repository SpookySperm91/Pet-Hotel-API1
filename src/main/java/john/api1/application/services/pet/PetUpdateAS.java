package john.api1.application.services.pet;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.domain.models.PetDomain;
import john.api1.application.dto.request.PetRequestDTO;
import john.api1.application.ports.repositories.pet.IPetUpdateRepository;
import john.api1.application.ports.repositories.pet.IPetsSearchRepository;
import john.api1.application.ports.services.IPetUpdate;
import john.api1.application.services.response.PetUpdateResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.function.BooleanSupplier;
import java.util.logging.Logger;

@Service
public class PetUpdateAS implements IPetUpdate {
    private static final Logger logger = Logger.getLogger(PetUpdateAS.class.getName());
    private final IPetUpdateRepository petUpdate;
    private final IPetsSearchRepository petSearch;

    @Autowired
    public PetUpdateAS(IPetUpdateRepository petUpdate, IPetsSearchRepository petSearch) {
        this.petUpdate = petUpdate;
        this.petSearch = petSearch;
    }

    // Check pets id if exist
    // Mapped to domain
    // Save to db and return boolean response
    @Override
    public DomainResponse<String> updatePet(String petId, PetRequestDTO pet) {
        try {
            if (!isValidId(petId)) return DomainResponse.error("Invalid pet ID format.");
            if (!petSearch.existsById(petId)) return DomainResponse.error("Pet does not exist.");

            PetDomain petDomain = PetDomain.updateFull(
                    petId,
                    pet.getOwnerId(),
                    pet.getPetName(),
                    pet.getAnimalType(),
                    pet.getBreed(),
                    pet.getSize(),
                    pet.getSpecialDescription(),
                    pet.getProfilePictureUrl()
            );

            return petUpdate.updatePet(petDomain) ?
                    DomainResponse.success(petId, "Pet '" + pet.getPetName() + "' successfully updated.") :
                    DomainResponse.error("Failed to update pet '" + pet.getPetName() + "'.");

        } catch (DomainArgumentException e) {
            logger.warning("Domain argument error for pet ID " + petId + ": " + e.getMessage());
            return DomainResponse.error(e.getMessage());
        } catch (DataAccessException e) {
            logger.severe("Database error while updating pet ID " + petId + ": " + e.getMessage());
            return DomainResponse.error("Unexpected database error. Please try again later.");
        }
    }

    // UPDATE SPECIFICS METHODS
    // Check pet id if exist
    // Return with updated value, error message otherwise
    //
    //
    @Override
    public DomainResponse<PetUpdateResponse> updatePetName(String petId, String newPetName) {
        return update(petId, () -> petUpdate.updatePetName(petId, newPetName),
                new PetUpdateResponse(petId, newPetName, null, null, null),
                "Unable to update pet name. Please try again later.");
    }

    @Override
    public DomainResponse<PetUpdateResponse> updatePetTypeAndBreed(String petId, String type, String breed) {
        return update(petId, () -> petUpdate.updatePetTypeAndBreed(petId, type, breed),
                new PetUpdateResponse(petId, null, type, breed, null),
                "Unable to update pet type and breed. Please try again later.");
    }

    @Override
    public DomainResponse<PetUpdateResponse> updatePetProfilePicture(String petId, String profilePicUrl) {
        return update(petId, () -> petUpdate.updatePetProfilePicture(petId, profilePicUrl),
                new PetUpdateResponse(petId, null, null, null, profilePicUrl),
                "Unable to update pet profile picture. Please try again later.");
    }

    // privates
    private boolean isValidId(String petId) {
        return ObjectId.isValid(petId);
    }

    private DomainResponse<PetUpdateResponse> update(String petId, BooleanSupplier updateOperation,
                                                     PetUpdateResponse response, String errorMessage) {
        if (!isValidId(petId)) return DomainResponse.error("Invalid pet ID format.");
        if (!updateOperation.getAsBoolean()) {
            logger.warning("Update operation failed for pet ID: " + petId + " - " + errorMessage);
            return DomainResponse.error(errorMessage);
        }
        return DomainResponse.success(response);
    }
}
