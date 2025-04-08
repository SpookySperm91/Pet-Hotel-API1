package john.api1.application.domain.models.request;

import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Getter
public class RequestDomain {
    private final String id;
    private final String petId;
    private final String ownerId;
    private final String boardingId;
    private RequestType requestType;
    private RequestStatus requestStatus;
    private String description;
    private Instant requestTime;
    private Instant resolvedTime;
    private String rejectionMessage;
    private boolean active;

    public static RequestDomain create(String petId, String ownerId, String boardingId, RequestType type, RequestStatus status, String description) {
        return new RequestDomain(null, petId, ownerId, boardingId, type, status, description, Instant.now(), Instant.now(), null, true);
    }

    public RequestDomain withId(String id) {
        return new RequestDomain(id, this.petId, this.ownerId, this.boardingId, this.requestType, this.requestStatus, this.description, this.requestTime, this.resolvedTime, this.rejectionMessage, this.active);
    }

    public void markStatus(RequestStatus newStatus) {
        Instant now = Instant.now();
        boolean isCompleted = this.requestStatus == RequestStatus.COMPLETED;
        boolean isLateRejected = this.requestStatus == RequestStatus.REJECTED &&
                this.resolvedTime.isBefore(now.minus(5, ChronoUnit.MINUTES));

        if (isCompleted || isLateRejected) {
            throw new DomainArgumentException("Cannot change status after completion or after 5 minutes of rejection");
        }

        this.requestStatus = newStatus;
        this.resolvedTime = Instant.now();

        if (newStatus == RequestStatus.COMPLETED) {
            this.active = false;
        }
    }

    public void stateRejectionReason(String message)    {
        if (message == null || message.trim().isEmpty()) throw new DomainArgumentException("Message cannot be empty");
        this.rejectionMessage = message;
        this.resolvedTime = Instant.now();
    }
}
