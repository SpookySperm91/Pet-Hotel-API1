package john.api1.application.dto.mapper.history;

import john.api1.application.components.DateUtils;
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
    private String duration;
    private String durationType;
    private String currentEnd;
    private String newEnd;
    private Double price;

    public ActivityLogExtensionRequestDTO(
            String id, String activityType, String requestType, String description, String performBy, String timestamp, String petName, String petType, String breed, String size, String owner,
            String duration, String durationType, Instant current, Instant end, Double price) {

        super(id, activityType, requestType, description, performBy, timestamp, petName, petType, breed, size, owner);
        this.duration = duration;
        this.durationType = durationType;
        this.currentEnd = DateUtils.formatInstantWithTime(current);
        this.newEnd = DateUtils.formatInstantWithTime(end);
        this.price = price;
    }
}
