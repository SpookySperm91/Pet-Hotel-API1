package john.api1.application.dto.mapper.boarding;

import john.api1.application.domain.models.boarding.BoardingPricingDomain;

import java.time.Instant;
import java.util.List;

public record BoardingReleasedDTO(
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
        String notes,

        // Pricing breakdown
        String paymentStatus,
        double initialPrice,
        List<RequestBreakdownDTO> requestBreakdown,
        double total,

        // Created at
        Instant createdAt
) {
}
