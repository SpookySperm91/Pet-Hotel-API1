package john.api1.application.ports.repositories.request;

import jakarta.annotation.Nullable;
import john.api1.application.components.enums.GroomingType;

import java.time.Instant;

// For history log aggregation for now...
public record GroomingCQRS(
        GroomingType groomingType,
        double price,
        @Nullable String description,
        Instant createdAt,
        Instant updatedAt
) {
}
