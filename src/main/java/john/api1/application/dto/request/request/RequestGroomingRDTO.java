package john.api1.application.dto.request.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestGroomingRDTO {
    @NotBlank(message = "Owner id cannot be empty")
    private String ownerId;
    @NotBlank(message = "Pet id cannot be empty")
    private String petId;
    @NotBlank(message = "Boarding id cannot be empty")
    private String boardingId;
    @NotBlank(message = "Extension type cannot be empty")
    @Pattern(regexp = "(?i)BASIC|FULL", message = "Grooming service should be: HOURS or DAYS")
    private String groomingService;
    @NotBlank(message = "Description cannot be empty")
    private String description;
}
