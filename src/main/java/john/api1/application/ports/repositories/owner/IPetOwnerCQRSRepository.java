package john.api1.application.ports.repositories.owner;


import java.util.List;
import java.util.Optional;

public interface IPetOwnerCQRSRepository {
    Optional<PetOwnerCQRS> getDetails(String id);
    Optional<List<String>> getAllPets(String id);
    Optional<String> checkPetIfExist(String owner, String pet);

    Optional<String> getPetOwnerName(String ownerId);
}
