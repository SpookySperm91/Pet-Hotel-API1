package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginPhoneNumberRequestDTO {
    @NotEmpty(message = "Phone number cannot be empty")
    private String phoneNumber;
    private String password;
}
