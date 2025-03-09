package john.api1.application.services.response;


import john.api1.application.dto.mapper.RegisterResponseDTO;

public record RegisterResponse(String accountId, String username, String email, String phoneNumber, String smsMessage) {
    public RegisterResponseDTO toDTO() {
        return new RegisterResponseDTO(accountId, username, email, phoneNumber, smsMessage);
    }
}
