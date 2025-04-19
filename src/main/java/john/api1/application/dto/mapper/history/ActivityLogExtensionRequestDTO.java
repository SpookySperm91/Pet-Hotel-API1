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
}
