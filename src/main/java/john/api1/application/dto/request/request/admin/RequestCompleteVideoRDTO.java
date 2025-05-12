package john.api1.application.dto.request.request.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import john.api1.application.components.annotation.ValidVideo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCompleteVideoRDTO {
    @NotBlank(message = "Request id cannot be empty")
    private String requestId;
    @NotBlank(message = "Video file fileName cannot be empty")
    @ValidVideo(message = "Please provide a valid video file with one of the supported extensions (.mp4, .avi, .mov).")
    private String videoFile;
    @Nullable
    private String notes;
}

