package john.api1.application.dto.mapper.history;

import java.time.Instant;

public record ActivityLogBoardingDTO(
        String id,
        String activityType,
        String description,
        String performedBy,
        Instant timestamp,
        Double price,

        // Pet information
        String petName,
        String animalType,
        String breed,
        String size,
        // Owner
        String ownerName,
        // Boarding infos
        String boardingType,
        int duration,
        Instant startDate,
        Instant endDate
) implements ActivityLogDTO {
}
