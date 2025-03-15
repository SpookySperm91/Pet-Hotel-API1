package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetRequestDTO {
    @NotBlank(message = "Owner's id cannot be empty")
    private String ownerId;
    @NotBlank(message = "Pet's name cannot be empty")
    private String petName;
    @NotBlank(message = "Pet's type cannot be empty")
    private String animalType;
    @NotBlank(message = "Pet's breed cannot be empty")
    private String breed;
    @NotBlank(message = "Size cannot be empty")
    private String size;
    private String specialDescription;
    private String profilePictureUrl;
}
