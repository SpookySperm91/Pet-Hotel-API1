package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.PositiveOrZero;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
public class BoardingRDTO {
    // all error messages should be aggregate end with "cannot be empty" in the controller!
    ////////
    @NotBlank(message = "pet id cannot be empty")
    private final String petId;
    @NotBlank(message = "owner id cannot be empty")
    private final String ownerId;
    @NotNull(message = "boarding start time cannot be empty")
    private Instant boardingStart;
    @NotNull(message = "boarding end time cannot be empty")
    private Instant boardingEnd;
@NotNull(message = "initial payment cannot be empty")
    @PositiveOrZero(message = "initial payment must be 0 or greater")
    private Double initialPayment;
    @NotBlank(message = "payment status cannot be empty")
    private String paymentStatus;
    private String notes;
}
