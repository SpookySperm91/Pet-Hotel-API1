package john.api1.application.dto.request.request.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import john.api1.application.components.annotation.ValidPhoto;
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
    @Valid
    @NotEmpty(message = "Photos list cannot be empty")
    @Size(max = 5, message = "You can upload at most 5 photos")
    private List<PhotoFile> photos;
    @Nullable
    private String notes;
    private Instant requestAt;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class PhotoFile {
        @NotBlank(message = "Photo file fileName cannot be blank")
        @ValidPhoto(message = "Please provide a valid image file with one of the supported extensions (.jpg, .jpeg, .png, .gif).")
        private String name;
    }

}

