package john.api1.application.services.response;

public record SessionResponse(String authorizedId, String token, String session) {
}
