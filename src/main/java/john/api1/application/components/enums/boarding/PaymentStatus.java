package john.api1.application.components.enums.boarding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {
    PAID("PAID"),
    NOT_PAID("NOT_PAID"),
    PENDING("PENDING");

    private final String paymentStatus;
}
