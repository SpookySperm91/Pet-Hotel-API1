package john.api1.application.dto.mapper.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityLogExtensionRequestDTO extends ActivityLogRequestDTO {
    private long duration;
    private String durationType;
    private Instant currentEnd;
    private Instant newEnd;
    private Double price;

    public ActivityLogExtensionRequestDTO(
            String id, String activityType, String description, String performBy, Instant timestamp, String petName, String petType, String breed, String size, String owner,
            long duration, String durationType, Instant current, Instant end, Double price) {

        super(id, activityType, description, performBy, timestamp, petName, petType, breed, size, owner);
        this.duration = duration;
        this.durationType = durationType;
        this.currentEnd = current;
        this.newEnd = end;
        this.price = price;
    }
}
