package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginPhoneNumberRDTO {
    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
