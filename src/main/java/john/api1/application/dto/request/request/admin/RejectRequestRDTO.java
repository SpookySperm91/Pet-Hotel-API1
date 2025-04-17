package john.api1.application.dto.request.request.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class RejectRequestRDTO {
    // @XSSSanitize
    @NotBlank(message = "Request id cannot be empty")
    private String requestId;

    // @XSSSanitize
    @NotBlank(message = "Rejection message cannot be empty")
    private String message;
}
