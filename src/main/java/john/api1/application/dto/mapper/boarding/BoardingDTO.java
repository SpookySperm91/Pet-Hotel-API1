package john.api1.application.dto.mapper.boarding;

import java.time.Instant;
import java.util.List;

public record BoardingDTO(
        // O(1)
        String id,
        String petId,
        String ownerId,

        // Pet
        String photoId,
        String photoUrl,
        Instant expiredAt,
        String petName,
        String petType,
        String petBreed,
        String petSize,
        int age,

        // Owner
        String ownerName,
        String ownerEmail,
        String ownerPhoneNumber,
        String ownerAddress,

        // Boarding details
        String boardingStatus,
        String boardingType,
        Instant boardingStart,
        Instant boardingEnd,
        Instant extensionEnd, // for extended time boarding
        Instant releasedAt,
        long durationDays,
        long durationHours,
        String notes,

        // Pricing breakdown
        String boardingPrepaidStatus,
        double rate,
        double boardingPrice,
        List<RequestBreakdownDTO> requestBreakdown,
        double total,

        // Created at
        Instant createdAt,
        boolean overdue
) {
}
