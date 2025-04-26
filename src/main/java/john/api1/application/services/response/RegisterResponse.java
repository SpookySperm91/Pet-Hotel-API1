package john.api1.application.services.response;


import john.api1.application.dto.mapper.RegisterDTO;

public record RegisterResponse(String accountId, String username, String email, String phoneNumber, String smsMessage) {
    public RegisterDTO toDTO() {
        return new RegisterDTO(accountId, username, email, phoneNumber, smsMessage);
    }
}
