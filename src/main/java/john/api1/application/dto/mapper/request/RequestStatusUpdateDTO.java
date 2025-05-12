package john.api1.application.dto.mapper.request;

public record RequestStatusUpdateDTO(
        String requestId,
        String requestType,
        String status,
        String ownerId,
        String ownerName,
        String petName
) {
}
