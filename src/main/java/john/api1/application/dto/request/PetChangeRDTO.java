package john.api1.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PetChangeRDTO {
    @NotEmpty
    String petId;
    @NotEmpty
    String ownerId;
    @Nullable
    String petName;
    @Nullable
    String breed;
    @Nullable
    String size;
    @Nullable
    Integer age;

    @Nullable
    String profilePhoto;

}
