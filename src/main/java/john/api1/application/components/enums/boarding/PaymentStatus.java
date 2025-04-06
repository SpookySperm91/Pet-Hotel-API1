package john.api1.application.components.enums.boarding;

import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum PaymentStatus {
    PAID("PAID"),
    NOT_PAID("NOT_PAID"),
    PENDING("PENDING");

    private final String paymentStatus;

    public static PaymentStatus fromStringOrDefault(String dbValue) {
        return Arrays.stream(PaymentStatus.values())
                .filter(bt -> bt.paymentStatus.equalsIgnoreCase(dbValue))
                .findFirst()
                .orElse(PaymentStatus.PENDING);
    }

    // High usage in Controller?
    public static PaymentStatus fromStringOrError(String dbValue) {
        return Arrays.stream(PaymentStatus.values())
                .filter(bt -> bt.paymentStatus.equalsIgnoreCase(dbValue))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException ("Boarding type request value '" + dbValue + "' cannot be converted to enum"));
    }

    public static PaymentStatus safeFromStringOrDefault(String dbValue) {
        return Arrays.stream(PaymentStatus.values())
                .filter(bt -> bt.paymentStatus.equalsIgnoreCase(dbValue))
                .findFirst()
                .orElseThrow(() -> new DomainArgumentException("Invalid payment status: '" + dbValue + "'. Valid statuses are: 'PAID', 'NOT_PAID', 'PENDING'"));
    }
}
