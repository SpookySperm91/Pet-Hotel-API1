package john.api1.application.domain.models;

import jakarta.annotation.Nullable;
import john.api1.application.components.enums.ActivityLogType;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.request.RequestDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class ActivityLogDomain {
    private final String id;
    @Nullable
    private String typeId;  // Request, Boarding
    private ActivityLogType activityType;
    @Nullable
    private RequestType requestType;  // For request
    private String performedBy;
    private String petOwner;
    private String pet;
    private String description;
    private Instant timestamp;

    // Admin
    public static ActivityLogDomain create(ActivityLogType activityType, String petOwner, String pet, String description) {
        return new ActivityLogDomain(null, null, activityType, null, "Admin", petOwner, pet, description, Instant.now());
    }

    public static ActivityLogDomain create(String typeId, ActivityLogType activityType, String performedBy, String petOwner, String pet, String description) {
        if (!ObjectId.isValid(typeId))
            throw new DomainArgumentException("Type id for activity log is invalid cannot be converted to ObjectId");

        return new ActivityLogDomain(null, typeId, activityType, null, performedBy, petOwner, pet, description, Instant.now());
    }

    public static ActivityLogDomain createForRequest(RequestDomain request, ActivityLogType activityType, String description, String petOwner, String pet) {
        if (!ObjectId.isValid(request.getId()))
            throw new DomainArgumentException("Request id for activity log is invalid cannot be converted to ObjectId");

        return new ActivityLogDomain(null, request.getId(), activityType, request.getRequestType(), "Admin", petOwner, pet, description, Instant.now());
    }

    public static ActivityLogDomain createForBoarding(BoardingDomain boarding, ActivityLogType activityType, String petOwner, String pet, String description) {
        if (!ObjectId.isValid(boarding.getId()))
            throw new DomainArgumentException("Boarding id for activity log is invalid cannot be converted to ObjectId");

        return new ActivityLogDomain(null, boarding.getId(), activityType, null, "Admin", petOwner, pet, description, Instant.now());
    }


    public ActivityLogDomain mapWithId(String id) {
        if (!ObjectId.isValid(id))
            throw new DomainArgumentException("Activity log id is invalid cannot be converted to ObjectId");

        return new ActivityLogDomain(id, this.typeId, this.activityType, this.requestType, this.performedBy, this.petOwner, this.pet, this.description, this.timestamp);
    }


}
