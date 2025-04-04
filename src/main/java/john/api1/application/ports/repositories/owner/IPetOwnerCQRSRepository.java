package john.api1.application.ports.repositories.owner;


import java.util.Optional;

public interface IPetOwnerCQRSRepository {
    Optional<PetOwnerCQRS> getDetails(String id);
}
