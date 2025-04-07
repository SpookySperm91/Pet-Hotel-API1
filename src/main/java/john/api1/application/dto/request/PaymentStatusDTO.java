package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusDTO {
    @NotEmpty(message = "Boarding id cannot be empty")
    private String id;
    @NotBlank(message = "Status cannot be empty")
    @Pattern(regexp = "(?i)PAID|NOT_PAID|PENDING", message = "Status must be one of: PAID, NOT_PAID, PENDING")
    private String status;
}

