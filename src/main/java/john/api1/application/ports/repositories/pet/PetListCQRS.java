package john.api1.application.ports.repositories.pet;

public record PetListCQRS(
        String id,
        String petName,
        String animalType,
        boolean boarding
) {
}
