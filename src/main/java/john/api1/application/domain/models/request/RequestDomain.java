package john.api1.application.domain.models.request;

import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

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



    public void updateStatus(RequestStatus newStatus) {
        if (this.requestStatus == RequestStatus.COMPLETED) {
            throw new DomainArgumentException("Cannot change status after completion");
        }
    }

    public void stateRejectionReason(String message) {
        if(message == null || message.isEmpty()) throw new DomainArgumentException("Message cannot be empty");
        this.rejectionMessage = message;
    }
}
