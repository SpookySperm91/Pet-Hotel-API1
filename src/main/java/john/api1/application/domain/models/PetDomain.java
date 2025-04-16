package john.api1.application.domain.models;

import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class PetDomain {
    private final String id;
    private final String ownerId;
    private String petName;
    private String animalType;
    private String breed;
    private String size;
    private int age;
    private String specialDescription;
    private String profilePictureUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean boarding;

    public static PetDomain create(String ownerId, String petName, String animalType, int age) {
        validateNotEmpty(ownerId, "Owner ID cannot be empty");
        validateNotEmpty(petName, "Pet fileName cannot be empty");
        validateNotEmpty(animalType, "Animal type cannot be empty");

        return new PetDomain(null, ownerId, petName, animalType, null, null, age, null, null, Instant.now(), Instant.now(), false);
    }

    public static PetDomain createFull(String ownerId, String petName, String animalType,
                                       String breed, String size, int age, String specialDescription, String profilePictureUrl) {
        validateNotEmpty(ownerId, "Owner ID cannot be empty");
        validateNotEmpty(petName, "Pet fileName cannot be empty");
        validateNotEmpty(animalType, "Animal type cannot be empty");

        return new PetDomain(null, ownerId, petName, animalType, breed, size, age, specialDescription, profilePictureUrl, Instant.now(), Instant.now(), false);
    }

    public static PetDomain updateFull(String id, String ownerId, String petName, String animalType,
                                       String breed, String size, int age, String specialDescription, String profilePictureUrl) {
        validateNotEmpty(id, "Pet ID cannot be empty");
        validateNotEmpty(ownerId, "Owner ID cannot be empty");
        validateNotEmpty(petName, "Pet fileName cannot be empty");
        validateNotEmpty(animalType, "Animal type cannot be empty");

        return new PetDomain(id, ownerId, petName, animalType, breed, size, age, specialDescription, profilePictureUrl, Instant.now(), Instant.now(), false);
    }

    public void updatePetName(String petName) {
        validateNotEmpty(petName, "Pet fileName cannot be empty");
        this.petName = petName;
        this.updatedAt = Instant.now();
    }

    public void updateAnimalType(String animalType) {
        validateNotEmpty(animalType, "Animal type cannot be empty");
        this.animalType = animalType;
        this.updatedAt = Instant.now();
    }

    public void updatePetInfo(String breed, String size, String specialDescription) {
        validateNotEmpty(breed, "Breed cannot be empty");
        validateNotEmpty(size, "Size cannot be empty");

        this.breed = breed;
        this.size = size;
        this.specialDescription = specialDescription;
        this.updatedAt = Instant.now();
    }

    public void updateProfilePicture(String url) {
        validateNotEmpty(url, "Profile picture URL cannot be empty");
        this.profilePictureUrl = url;
        this.updatedAt = Instant.now();
    }

    private static void validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainArgumentException(message);
        }
    }

    public void boardPet() {
        if (boarding) throw new DomainArgumentException("Pet is currently boarding");
        this.boarding = true;
        this.updatedAt = Instant.now();
    }

    public void unBoardPet() {
        if (!boarding) throw new DomainArgumentException("Pet is not currently boarding");
        this.boarding = false;
        this.updatedAt = Instant.now();
    }
}
