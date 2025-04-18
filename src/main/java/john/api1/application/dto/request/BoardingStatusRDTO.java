package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardingStatusRDTO {
        @NotEmpty(message = "Id cannot be empty")
        private String id;
        @NotBlank(message = "Status cannot be empty")
        @Pattern(regexp = "(?i)BOARDING|DONE_BOARDING|OVERDUE|RELEASED", message = "Boarding status must be one of: BOARDING, DONE_BOARDING, OVERDUE, RELEASED.")
        private String status;
}

