package john.api1.application.dto.mapper.history;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityLogGroomingRequestDTO extends ActivityLogRequestDTO {
    private String groomingType;
    private Double price;
}
