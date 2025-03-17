package john.api1.application.services.pet;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.PetDomain;
import john.api1.application.dto.request.PetRequestDTO;
import john.api1.application.ports.repositories.pet.IPetCreateRepository;
import john.api1.application.ports.services.IPetRegister;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetRegistrationAS implements IPetRegister {
    private final IPetCreateRepository petCreate;

    @Autowired
    public PetRegistrationAS(IPetCreateRepository petCreate) {
        this.petCreate = petCreate;
    }

    @Override
    public DomainResponse<String> registerPet(PetRequestDTO registerPet) {
        try {
            // check if pet owner's id is convertable to ObjectId
            if (!ObjectId.isValid(registerPet.getOwnerId())) {
                throw new DomainArgumentException("Invalid Owner ID format");
            }

            PetDomain petDomain = PetDomain.createFull(
                    registerPet.getOwnerId(),
                    registerPet.getPetName(),
                    registerPet.getAnimalType(),
                    registerPet.getBreed(),
                    registerPet.getSize(),
                    registerPet.getSpecialDescription(),
                    null
            );

            String petId = petCreate.createNewPet(petDomain)
                    .orElseThrow(() -> new PersistenceException("Failed to register pet."));

            return DomainResponse.success(
                    petId,
                    "Pet '" + petDomain.getPetName() + "' has been successfully registered."
            );
        } catch (DomainArgumentException e) {
            return DomainResponse.error("Invalid input: " + e.getMessage());

        } catch (PersistenceException e) {
            return DomainResponse.error("Unable to register pet. Please try again later.");
        }
    }
}
