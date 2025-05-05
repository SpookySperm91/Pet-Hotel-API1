package john.api1.application.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class AdminChangeRDTO {
    @NotBlank(message = "admin id cannot be empty")
    private String id;
    @NotBlank(message = "cannot be empty")
    private String data;
}
