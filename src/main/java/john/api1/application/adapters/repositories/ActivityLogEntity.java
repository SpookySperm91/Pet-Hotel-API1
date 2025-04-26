package john.api1.application.adapters.repositories;

import jakarta.annotation.Nullable;
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
    @Nullable
    @Indexed(name = "typeId_sparse_idx", sparse = true)
    private ObjectId typeId;  // boarding, request
    private String activityType;
    @Nullable
    private String requestType;
    private String performedBy;
    private String petOwner;
    private String pet;
    private String description;
    @CreatedDate
    @Indexed
    private Instant createdAt;

    public static ActivityLogEntity mapDomain(ActivityLogDomain domain) {
        if (domain.getTypeId() != null && !ObjectId.isValid(domain.getTypeId()))
            throw new PersistenceException("Invalid animalType id cannot be converted to ObjectId");

        return new ActivityLogEntity(
                null,
                domain.getTypeId() != null && !domain.getTypeId().isEmpty() ? new ObjectId(domain.getTypeId()) : null,
                domain.getActivityType().getActivityLogType(),
                domain.getRequestType() != null ? domain.getRequestType().getRequestType() : null,
                domain.getPerformedBy(),
                domain.getPetOwner(),
                domain.getPet(),
                domain.getDescription(),
                domain.getTimestamp());
    }
}
