package john.api1.application.services.response;


public record RegisterResponse(String clientId, String username, String email, String phoneNumber, String smsMessage) {}

