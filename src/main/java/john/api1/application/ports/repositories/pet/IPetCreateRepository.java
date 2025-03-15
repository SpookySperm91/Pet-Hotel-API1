package john.api1.application.ports.repositories.pet;

import john.api1.application.domain.models.PetDomain;

import java.util.Optional;

public interface IPetCreateRepository {
    Optional<String> createNewPet(PetDomain pet);
}
