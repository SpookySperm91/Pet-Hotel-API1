package john.api1.application.dto.mapper.history;


public record ActivityLogBoardingDTO(
        String id,
        String activityType,
        String description,
        String performedBy,
        String timestamp,

        // Pet information
        String petName,
        String animalType,
        String breed,
        String size,
        // Owner
        String ownerName,
        // Boarding infos
        String boardingType,
        String duration,
        Double price,
        String startDate,
        String endDate
) implements ActivityLogDTO {
}
