package john.api1.application.ports.repositories.pet;

public record PetCQRS(
        String profilePhoto,
        String petName,
        String animalType,
        String breed,
        String size,
        int age,
        String specialDescription,
        boolean boarding) {


    public static PetCQRS mapNameTypeBreed(String name, String type, String breed) {
        return new PetCQRS(null, name, type, breed, null, 0, null, false);
    }

    public static PetCQRS mapReleased(String profile, String name, String type, String breed, String size, int age) {
        return new PetCQRS(profile, name, type, breed, size, age, null, false);
    }
}
