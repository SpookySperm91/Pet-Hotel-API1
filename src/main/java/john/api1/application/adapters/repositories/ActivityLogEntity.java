package john.api1.application.adapters.repositories;

import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.ActivityLogDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "history_log")
public class ActivityLogEntity {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId typeId;
    private String activityType;
    private String performedBy;
    private String petOwner;
    private String pet;
    private String description;
    @CreatedDate
    @Indexed
    private Instant createdAt;

    public static ActivityLogEntity mapDomain(ActivityLogDomain domain) {
        if (!ObjectId.isValid(domain.getTypeId()))
            throw new PersistenceException("Invalid type id cannot be converted to ObjectId");

        return new ActivityLogEntity(
                null,
                new ObjectId(domain.getTypeId()),
                domain.getActivityType().getActivityLogType(),
                domain.getPerformedBy(),
                domain.getPetOwner(),
                domain.getPet(),
                domain.getDescription(),
                domain.getTimestamp());
    }
}
