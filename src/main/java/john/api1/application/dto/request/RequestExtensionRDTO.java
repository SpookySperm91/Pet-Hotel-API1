package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestExtensionRDTO {
    @NotBlank(message = "Owner id cannot be empty")
    private String ownerId;
    @NotBlank(message = "Pet id cannot be empty")
    private String petId;
    @NotBlank(message = "Extension type cannot be empty")
    @Pattern(regexp = "(?i)HOURS|DAYS", message = "Extension type should be: HOURS or DAYS")
    private String extensionType;
    @NotNull(message = "Extension duration cannot be empty")
    @DecimalMin(value = "1.0", inclusive = true, message = "Extension duration must be a whole number and at least 1")
    @Digits(integer = 10, fraction = 0, message = "Extension duration must be a whole number")
    private BigDecimal extensionDuration;
    @NotBlank(message = "Description cannot be empty")
    private String description;
}
