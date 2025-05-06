package john.api1.application.ports.repositories.pet;

public record PetCQRS(
        String id,
        String profilePhoto,
        String petName,
        String animalType,
        String breed,
        String size,
        int age,
        String specialDescription,
        boolean boarding) {


    public static PetCQRS mapNameTypeBreed(String id, String name, String type, String breed) {
        return new PetCQRS(id, null, name, type, breed, null, 0, null, false);
    }

    public static PetCQRS mapNameTypeBreedSize(String id, String name, String type, String breed, String size) {
        return new PetCQRS(id, null, name, type, breed, size, 0, null, false);
    }

    public static PetCQRS mapReleased(String id, String profile, String name, String type, String breed, String size, int age) {
        return new PetCQRS(id, profile, name, type, breed, size, age, null, false);
    }
}
