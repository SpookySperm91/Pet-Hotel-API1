package john.api1.application.dto.mapper.owner;

public record PetOwnerPendingDTO(
        String id,
        String fullName,
        String email,
        String phoneNumber,
        String address,
        String registrationDate
) {
}
