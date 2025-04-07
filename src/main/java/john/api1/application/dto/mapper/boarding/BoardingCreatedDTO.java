package john.api1.application.dto.mapper.boarding;

import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

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
        double initialPrice,
        double total,

        // Created at
        Instant createdAt
) {
}
