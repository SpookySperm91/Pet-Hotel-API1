package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class BoardingRDTO {
    // all error messages should be aggregate end with "cannot be empty" in the controller!
    ////////
    @NotBlank(message = "pet id cannot be empty")
    private final String petId;
    @NotBlank(message = "owner id cannot be empty")
    private final String ownerId;
    @NotBlank(message = "Boarding type cannot be empty")
    @Pattern(regexp = "(?i)DAYCARE|LONG_STAY", message = "Boarding type must be: DAYCARE or LONG_STAY")
    private final String boardingType;
    @NotNull(message = "boarding start time cannot be empty")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH")  // Hour precision only
    private final LocalDateTime  boardingStart;
    @NotNull(message = "boarding end time cannot be empty")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH")  // Hour precision only
    private final LocalDateTime  boardingEnd;
    @NotBlank(message = "payment status cannot be empty")
    @Pattern(regexp = "(?i)PAID|NOT_PAID", message = "Payment status must be: PAID or NOT_PAID")
    private final String paymentStatus;
    private final String notes;
}
