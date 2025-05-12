package john.api1.application.dto.mapper.pet;


import jakarta.annotation.Nullable;


public record PetBoardingHistoryDTO(
        @Nullable String id,
        @Nullable String boardingType,
        @Nullable String duration,
        @Nullable String notes,
        @Nullable Double price,
        @Nullable String paymentStatus,
        @Nullable String checkIn,
        @Nullable String checkOut

) {
}
