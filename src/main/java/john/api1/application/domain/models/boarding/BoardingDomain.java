package john.api1.application.domain.models.boarding;

import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Getter
public class BoardingDomain {
    private final String id;
    private final String petId;
    private final String ownerId;
    private BoardingType boardingType;
    private Instant boardingStart;
    private Instant boardingEnd;
    private BoardingStatus boardingStatus;
    private PaymentStatus paymentStatus;
    private Instant updatedAt;
    private boolean active;

    public void extendBoarding(long extraDays, boolean requiresAdditionalPayment) {
        if (extraDays <= 0) {
            throw new DomainArgumentException("Extension must be greater than zero days.");
        }

        this.boardingEnd = boardingEnd.plus(extraDays, ChronoUnit.DAYS);
        this.paymentStatus = requiresAdditionalPayment ? PaymentStatus.PENDING : this.paymentStatus;
        this.updatedAt = Instant.now();
    }

    public void updatePaymentStatus(PaymentStatus newPaymentStatus) {
        this.paymentStatus = newPaymentStatus;
        this.updatedAt = Instant.now();
    }

    public void updateBoardingStatus(BoardingStatus status) {
        if (!this.active) {
            throw new IllegalStateException("Cannot update status of an inactive boarding.");
        }

        this.boardingStatus = status;
        this.updatedAt = Instant.now();

        if (status == BoardingStatus.RELEASED) {
            this.active = false; // Mark as inactive when released
        }
    }
}
