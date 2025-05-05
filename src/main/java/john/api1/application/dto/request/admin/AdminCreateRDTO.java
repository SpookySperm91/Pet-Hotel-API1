package john.api1.application.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class AdminCreateRDTO {
    @NotBlank(message = "username cannot be empty")
    private String username;
    @NotBlank(message = "email cannot be empty")
    private String email;
    @NotBlank(message = "password cannot be empty")
    private String password;
}
