package john.api1.application.domain.models.boarding;

import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class BoardingDomain {
    private final String id;
    private final String petId;
    private final String ownerId;
    private BoardingType boardingType;
    private Instant boardingStart;
    private Instant boardingEnd; // final, cannot change, only in extension + aggregation
    private BoardingStatus boardingStatus;
    private PaymentStatus paymentStatus;
    private final String notes;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean active;

    public static BoardingDomain create(String petId, String ownerId, BoardingType boardingType, Instant boardingStart, Instant boardingEnd, PaymentStatus paymentStatus, String notes) {
        if (!ObjectId.isValid(petId)) throw new DomainArgumentException("Invalid pet-id format");
        if (!ObjectId.isValid(ownerId)) throw new DomainArgumentException("Invalid owner-id format");

        return new BoardingDomain(null, petId, ownerId,
                boardingType, boardingStart, boardingEnd, BoardingStatus.BOARDING, paymentStatus, notes,
                Instant.now(), Instant.now(),
                true
        );
    }

    public BoardingDomain copy() {
        return this.toBuilder().build();
    }

    public BoardingDomain withId(String id) {
        return new BoardingDomain(
                id, petId, ownerId, boardingType, boardingStart, boardingEnd,
                boardingStatus, paymentStatus, notes, createdAt, updatedAt, active
        );
    }

    public void extendBoarding(long extraDays) {
        if (extraDays <= 0) throw new DomainArgumentException("Extension must be greater than zero days.");


        this.boardingEnd = boardingEnd.plus(extraDays, ChronoUnit.DAYS);
        this.paymentStatus = PaymentStatus.PENDING;
        this.updatedAt = Instant.now();
    }

    public void daycareToLongDay() {
        if (this.boardingStart == null || this.boardingEnd == null)
            throw new DomainArgumentException("Boarding start or end time is missing.");

        Duration duration = Duration.between(this.boardingStart, this.boardingEnd);
        if (this.boardingType == BoardingType.DAYCARE && duration.toHours() >= 24) {
            this.boardingType = BoardingType.LONG_STAY;
            this.updatedAt = Instant.now();
        }
    }

    public void updatePaymentStatus(PaymentStatus newPaymentStatus) {
        this.paymentStatus = newPaymentStatus;
        this.updatedAt = Instant.now();
    }

    public void updateBoardingStatus(BoardingStatus status) {
        if (!this.active) throw new DomainArgumentException("Cannot update status of an inactive boarding.");

        this.boardingStatus = status;
        this.updatedAt = Instant.now();

        if (status == BoardingStatus.RELEASED) {
            this.active = false; // Mark as inactive when released
        }
    }

    public long getBoardingDuration() {
        if (boardingStart == null || boardingEnd == null)
            throw new DomainArgumentException("Boarding start and end time must not be null.");

        long duration;
        if (boardingType == BoardingType.DAYCARE) {
            // Calculate duration in hours for daycare
            duration = ChronoUnit.HOURS.between(boardingStart, boardingEnd);
        } else {
            // Calculate duration in days for long-stay, ensuring minimum 1 day
            duration = ChronoUnit.DAYS.between(boardingStart, boardingEnd);
            return duration < 1 ? 1 : duration; // If less than 1 day, count as 1 day
        }

        return duration > 0 ? duration : 0; // Ensure non-negative values
    }

    public long determineDuration() {
        int HOURS_PER_DAY = 24;
        long duration = getBoardingDuration();

        if (boardingType == BoardingType.LONG_STAY)
            return (long) Math.ceil(duration / (double) HOURS_PER_DAY);

        return duration;
    }


}
