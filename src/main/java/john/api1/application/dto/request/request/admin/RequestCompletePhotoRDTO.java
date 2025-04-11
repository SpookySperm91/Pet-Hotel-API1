package john.api1.application.dto.request.request.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCompletePhotoRDTO {
    @NotBlank(message = "Request id cannot be empty")
    private String requestId;
    @NotEmpty(message = "Photos list cannot be empty")
    @Size(max = 5, message = "You can upload at most 5 photos")
    private List<PhotoFile> photos;
    @Nullable
    private String notes;
    private Instant requestAt;
}

