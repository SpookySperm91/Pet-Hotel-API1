package john.api1.application.dto.request.request.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// FOR GROOMING AND BOARDING EXTENSION
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCompleteServiceRDTO {
    @NotBlank(message = "Grooming/Extension id cannot be empty")
    private String serviceId;
    @NotBlank(message = "Request id cannot be empty")
    private String requestId;
    @Nullable
    private String notes;
    private Instant requestAt;
}
