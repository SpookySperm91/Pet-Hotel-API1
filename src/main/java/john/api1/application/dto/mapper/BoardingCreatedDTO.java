package john.api1.application.dto.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

public record BoardingCreatedDTO(
        // O(1)
        String id,
        String petId,
        String ownerId,

        // Pet
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
        double initialPayment,
        String paymentStatus,
        String notes,
        Instant createdAt
) {
}
