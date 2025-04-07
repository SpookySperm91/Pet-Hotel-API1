package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetRDTO {
    @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid owner ID format")
    private String ownerId;
    @NotBlank(message = "Pet's name cannot be empty")
    private String petName;
    @NotBlank(message = "Pet's type cannot be empty")
    private String animalType;
    @NotBlank(message = "Pet's breed cannot be empty")
    private String breed;
    @NotBlank(message = "Size cannot be empty")
    private String size;
    @Min(value = 0, message = "Age must be a non-negative number")
    private int age;
    private String specialDescription;

}
