package john.api1.application.components.enums.boarding;

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
}
