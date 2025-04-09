package john.api1.application.dto.mapper.request;

public record RequestStatusUpdateDTO(
        String requestId,
        String status,
        String ownerName,
        String petName
) {
}
