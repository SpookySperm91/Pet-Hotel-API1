package john.api1.application.dto.mapper;


public record RegisterDTO(
        String clientId,
        String username,
        String email,
        String phoneNumber,
        String smsMessage) {
}
