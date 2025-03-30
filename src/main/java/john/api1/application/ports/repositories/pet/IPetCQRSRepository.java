package john.api1.application.ports.repositories.pet;


import java.util.Optional;

public interface IPetCQRSRepository {
    Optional<PetCQRS> getPetDetails(String id);
    Optional<PetCQRS> getPetNameBreedType(String id);
}
