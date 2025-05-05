package john.api1.application.services.pet;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.components.exception.PersistenceHistoryException;
import john.api1.application.domain.models.PetDomain;
import john.api1.application.dto.request.PetRDTO;
import john.api1.application.ports.repositories.owner.IPetOwnerUpdateRepository;
import john.api1.application.ports.repositories.pet.IPetCreateRepository;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.history.IHistoryLogCreate;
import john.api1.application.ports.services.pet.IPetRegister;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetRegistrationAS implements IPetRegister {
    private static final Logger log = LoggerFactory.getLogger(PetRegistrationAS.class);

    private final IPetCreateRepository petCreate;
    private final IPetOwnerUpdateRepository ownerUpdate;
    private final IPetOwnerSearch ownerSearch;
    private final IHistoryLogCreate historyCreate;

    @Autowired
    public PetRegistrationAS(IPetCreateRepository petCreate,
                             IPetOwnerUpdateRepository accountUpdate,
                             IPetOwnerSearch ownerSearch,
                             IHistoryLogCreate historyCreate) {
        this.petCreate = petCreate;
        this.ownerUpdate = accountUpdate;
        this.ownerSearch = ownerSearch;
        this.historyCreate = historyCreate;
    }

    // Register new pet
    // Check if owner exist
    // Instantiates pet domain
    // Update owner's pets-list
    // Save to DB
    // Log history
    // Return response
    @Override
    public DomainResponse<String> registerPet(PetRDTO registerPet) {
        String ownerId = registerPet.getOwnerId();

        try {
            // check if pet owner's id is convertable to ObjectId
            if (!ObjectId.isValid(ownerId))
                throw new DomainArgumentException("Invalid owner id format cannot bec converted to ObjectId");

            var owner = ownerSearch.getPetOwnerDetails(ownerId);
            if (owner.isEmpty()) return DomainResponse.error("Pet owner does not exist.");


            PetDomain petDomain = PetDomain.createFull(
                    registerPet.getOwnerId(),
                    registerPet.getPetName(),
                    registerPet.getAnimalType(),
                    registerPet.getBreed(),
                    registerPet.getSize(),
                    registerPet.getAge(),
                    registerPet.getSpecialDescription(),
                    null
            );

            var petId = petCreate.createNewPet(petDomain)
                    .orElseThrow(() -> new PersistenceException("Failed to register pet."));
            // update pet owner with new registered pet
            ownerUpdate.addNewPet(ownerId, petId);

            // history log
            try {
                historyCreate.createActivityLogPetRegister(petId, owner.get().ownerName(), registerPet.getPetName());
                log.info("Activity log created for new registered pet '{}' owned to '{}'", petDomain.getPetName(), owner.get().ownerName());
            } catch (PersistenceHistoryException e) {
                log.warn("Activity log for new pet registration failed to save in class 'PetRegistrationAS'");
            }

            return DomainResponse.success(petId, "Pet '" + petDomain.getPetName() + "' has been successfully registered."
            );
        } catch (DomainArgumentException e) {
            return DomainResponse.error("Invalid input: " + e.getMessage());

        } catch (PersistenceException e) {
            return DomainResponse.error("Unable to register pet. Please try again later.");
        }
    }
}
