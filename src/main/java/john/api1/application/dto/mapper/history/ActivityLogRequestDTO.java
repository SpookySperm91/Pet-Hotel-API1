package john.api1.application.dto.mapper.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityLogRequestDTO implements ActivityLogDTO{
    private String id;
    private String activityType;
    private String description;
    private String performedBy;
    private Instant timestamp;
    // Pet information
    private String petName;
    private String animalType;
    private String breed;
    private String size;
    // Owner
    private String ownerName;
}
