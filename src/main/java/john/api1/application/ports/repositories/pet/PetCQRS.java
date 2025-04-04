package john.api1.application.ports.repositories.pet;

public record PetCQRS(
        String petName,
        String animalType,
        String breed,
        String size,
        int age,
        String specialDescription,
        boolean boarding) {


    public static PetCQRS mapNameTypeBreed(String name, String type, String breed) {
        return new PetCQRS(name, type, breed, null, 0, null, false);
    }

    public static PetCQRS mapReleased(String name, String type, String breed, String size, int age) {
        return new PetCQRS(name, type, breed, size, age, null, false);
    }
}
