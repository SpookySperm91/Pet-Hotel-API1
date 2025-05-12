package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCreditRDTO {
    @NotBlank(message = "User id cannot be empty")
    private String id;
    @Nullable
    private String email;
    @Nullable
    private String phoneNumber;
}
