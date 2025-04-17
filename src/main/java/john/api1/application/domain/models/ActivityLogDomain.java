package john.api1.application.domain.models;

import john.api1.application.components.enums.ActivityLogType;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class ActivityLogDomain {
    private final String id;
    private final String typeId;
    private ActivityLogType activityType;
    private String performedBy;
    private String petOwner;
    private String pet;
    private String description;
    private Instant timestamp;

    public static ActivityLogDomain create(String typeId, ActivityLogType activityType, String performedBy, String petOwner, String pet, String description) {
        if(!ObjectId.isValid(typeId)) throw new DomainArgumentException("Type id for activity log is invalid cannot be converted to ObjectId");

        return new ActivityLogDomain(null, typeId, activityType, performedBy, petOwner, pet, description, Instant.now());
    }

    public  ActivityLogDomain mapWithId(String id) {
        if(!ObjectId.isValid(id)) throw new DomainArgumentException("Activity log id is invalid cannot be converted to ObjectId");

        return new ActivityLogDomain(id, this.typeId, this.activityType, this.performedBy, this.petOwner, this.pet, this.description, this.timestamp);
    }
}
