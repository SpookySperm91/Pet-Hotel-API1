package john.api1.application.domain.models.request;

import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Getter

public class RequestDomain {
    private final String id;
    private final String petId;
    private final String ownerId;
    private final String boardingId;
    @Setter
    private RequestType requestType;
    @Setter
    private RequestStatus requestStatus;
    @Setter
    private String description;
    @Setter
    private Instant requestTime;
    @Setter
    private Instant resolvedTime;
    @Setter
    private String responseMessage; // reject or after completing request
    @Setter
    private boolean active;

    public static RequestDomain create(String petId, String ownerId, String boardingId, RequestType type, RequestStatus status, String description) {
        return new RequestDomain(null, petId, ownerId, boardingId, type, status, description, Instant.now(), Instant.now(), null, true);
    }

    public RequestDomain withId(String id) {
        return new RequestDomain(id, this.petId, this.ownerId, this.boardingId, this.requestType, this.requestStatus, this.description, this.requestTime, this.resolvedTime, this.responseMessage, this.active);
    }

    public void markStatus(RequestStatus newStatus) {
        Instant now = Instant.now();
        boolean isLockedAfterCompleted = this.requestStatus == RequestStatus.COMPLETED &&
                newStatus != RequestStatus.ARCHIVED;
        boolean isArchived = this.requestStatus == RequestStatus.ARCHIVED;
        boolean isLateRejected = this.requestStatus == RequestStatus.REJECTED &&
                this.resolvedTime != null &&
                this.resolvedTime.isBefore(now.minus(5, ChronoUnit.MINUTES));

        if (isLockedAfterCompleted) {
            throw new DomainArgumentException("Request cannot change status after completion");
        }
        if (isArchived) {
            throw new DomainArgumentException("Request cannot change status of already archived");
        }
        if (isLateRejected) {
            throw new DomainArgumentException("Request cannot change status 5 minutes of rejection");
        }

        this.requestStatus = newStatus;
        this.resolvedTime = Instant.now();

        if (newStatus == RequestStatus.COMPLETED || newStatus == RequestStatus.ARCHIVED || newStatus == RequestStatus.CANCELLED) {
            this.active = false;
        }
    }
}
