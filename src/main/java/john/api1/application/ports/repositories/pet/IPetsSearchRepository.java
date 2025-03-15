package john.api1.application.ports.repositories.pet;

import john.api1.application.domain.models.PetDomain;

import java.util.List;
import java.util.Optional;

public interface IPetsSearchRepository {
    Optional<PetDomain> getPetById(String petId);
    boolean existsById(String petId);

    List<String> getAllPetsIdByOwner(String petOwnerId);

    List<String> getAllPetsIdByAnimalType(String animalType);

    List<String> getAllPetsId();
}
