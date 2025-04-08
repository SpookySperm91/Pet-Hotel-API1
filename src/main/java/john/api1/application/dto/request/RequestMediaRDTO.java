package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestMediaRDTO {
    @NotBlank(message = "Owner id cannot be empty")
    private String ownerId;
    @NotBlank(message = "Pet id cannot be empty")
    private String petId;
    @NotBlank(message = "Request media type cannot be empty")
    @Pattern(regexp = "(?i)PHOTO|VIDEO", message = "Boarding type must be: DAYCARE or LONG_STAY")
    private String requestType;
    @NotBlank(message = "Description cannot be empty")
    private String description;
}
