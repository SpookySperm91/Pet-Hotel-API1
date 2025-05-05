package john.api1.application.dto.mapper.boarding;

import java.time.Instant;

public record BoardingCreatedDTO(
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
        String boardingType,
        Instant boardingStart,
        Instant boardingEnd,
        String paymentStatus,
        String notes,

        // Pricing breakdown
        double rate,
        double boardingPrice,
        double total,

        // Created at
        Instant createdAt
) {
}
