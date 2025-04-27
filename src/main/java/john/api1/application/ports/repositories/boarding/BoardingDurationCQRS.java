package john.api1.application.ports.repositories.boarding;

import john.api1.application.components.enums.boarding.BoardingType;

import java.time.Instant;

public record BoardingDurationCQRS(
        Instant checkIn,
        Instant checkOut,
        BoardingType boardingType
) {
}
