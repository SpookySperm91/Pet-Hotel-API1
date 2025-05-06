package john.api1.application.ports.repositories.pet;


import java.util.List;
import java.util.Optional;

public interface IPetCQRSRepository {
    Optional<PetCQRS> getPetDetails(String id);
    Optional<PetCQRS> getPetNameBreed(String id);
    Optional<PetCQRS> getPetNameBreedSize(String id);
    Optional<String> getPetName(String id);

    List<PetCQRS> getAllByOwner(String ownerId);

}
