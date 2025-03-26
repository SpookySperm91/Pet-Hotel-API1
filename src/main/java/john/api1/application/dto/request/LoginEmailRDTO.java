package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginEmailRDTO {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    private String password;
}
