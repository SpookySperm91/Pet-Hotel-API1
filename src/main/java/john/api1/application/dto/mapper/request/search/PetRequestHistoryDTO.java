package john.api1.application.dto.mapper.request.search;

import jakarta.annotation.Nullable;

public record PetRequestHistoryDTO(
        @Nullable String id,
        @Nullable String requestType,
        @Nullable String dateRequest, // Feb 14, 2025
        @Nullable String description,
        @Nullable Double price,
        @Nullable String paymentStatus,
        @Nullable String status
) {
}
