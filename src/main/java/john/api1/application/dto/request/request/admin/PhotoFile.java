package john.api1.application.dto.request.request.admin;

import jakarta.validation.constraints.NotBlank;
import john.api1.application.components.anoitation.ValidPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoFile {
    @NotBlank(message = "Photo file name cannot be blank")
    @ValidPhoto(message = "Please provide a valid image file with one of the supported extensions (.jpg, .jpeg, .png, .gif).")
    private String name;
}
