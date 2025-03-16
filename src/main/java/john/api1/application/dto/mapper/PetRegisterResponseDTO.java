package john.api1.application.dto.mapper;

// register -> upload prof to storage -> update again
public record PetRegisterResponseDTO(String petId, String apiUrl) {
}
