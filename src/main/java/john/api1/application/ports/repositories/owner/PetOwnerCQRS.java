package john.api1.application.ports.repositories.owner;

public record PetOwnerCQRS(
        String ownerName,
        String ownerEmail,
        String ownerPhoneNumber,
        String streetAddress,
        String cityAddress,
        String stateAddress
) {
}
