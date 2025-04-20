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
public class ActivityLogGroomingRequestDTO extends ActivityLogRequestDTO {
    private String groomingType;
    private Double price;


    public ActivityLogGroomingRequestDTO(
            String id, String activityType, String requestType, String description, String performBy, Instant timestamp, String petName, String petType, String breed, String size, String owner,
            String groomingType, Double price) {

        super(id, activityType, requestType, description, performBy, timestamp, petName, petType, breed, size, owner);
        this.groomingType = groomingType;
        this.price = price;
    }
}
