package john.api1.application.domain.cores;

import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.domain.models.request.RequestDomain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class RequestStatusDS {
    public static void markStatus(RequestDomain domain, RequestStatus newStatus) {
        Instant now = Instant.now();
        boolean isLockedAfterCompleted = domain.getRequestStatus() == RequestStatus.COMPLETED &&
                newStatus != RequestStatus.ARCHIVED;
        boolean isArchived = domain.getRequestStatus() == RequestStatus.ARCHIVED;
        boolean isLateRejected = domain.getRequestStatus() == RequestStatus.REJECTED &&
                domain.getResolvedTime() != null &&
                domain.getResolvedTime().isBefore(now.minus(5, ChronoUnit.MINUTES));

        if (isLockedAfterCompleted) {
            throw new DomainArgumentException("Request cannot change status after completion");
        }
        if (isArchived) {
            throw new DomainArgumentException("Request cannot change status of already archived");
        }
        if (isLateRejected) {
            throw new DomainArgumentException("Request cannot change status 5 minutes of rejection");
        }

        domain.setRequestStatus(newStatus);
        domain.setResolvedTime(Instant.now());

        if (newStatus == RequestStatus.COMPLETED || newStatus == RequestStatus.ARCHIVED || newStatus == RequestStatus.CANCELLED) {
            domain.setActive(false);
        }
    }

    public static void deletable(RequestDomain domain) {
        if (domain.getRequestStatus() != RequestStatus.ARCHIVED
                && domain.getRequestStatus() != RequestStatus.REJECTED
                && domain.getRequestStatus() != RequestStatus.CANCELLED) {
            throw new DomainArgumentException(String.format("Request is %s and cannot be deleted",
                    domain.getRequestType().getRequestType().toLowerCase()));
        }
    }

    public static void isValidToCommit(RequestDomain domain) {
        mustNotBeFinalized(domain, "commit");
        if(domain.getRequestStatus() == RequestStatus.PENDING)
            throw new DomainArgumentException("Request is currently pending for approval and cannot be committed");
    }

    public static void isValidToApprove(RequestDomain domain) {
        mustNotBeFinalized(domain, "approve");
        if (domain.getRequestStatus() == RequestStatus.IN_PROGRESS)
            throw new DomainArgumentException("Request was already approved and is in progress");
    }

    public static void isValidToReject(RequestDomain domain) {
        mustNotBeFinalized(domain, "reject");
        if (domain.getRequestStatus() == RequestStatus.IN_PROGRESS)
            throw new DomainArgumentException("Request was already approved and is in progress. Revert it to pending to allow reject!");
    }

    public static void isValidToRevert(RequestDomain domain) {
        mustNotBeFinalized(domain, "revert to pending");
        if (domain.getRequestStatus() != RequestStatus.IN_PROGRESS)
            throw new DomainArgumentException("Cannot revert to pending. Request status is not in progress");
    }

    public static void isValidToUndoReject(RequestDomain domain) {
        if (domain.getRequestStatus() == RequestStatus.CANCELLED)
            throw new DomainArgumentException("Request was cancelled and cannot be change undo rejected");
        if (domain.getRequestStatus() != RequestStatus.REJECTED)
            throw new DomainArgumentException("Cannot undo reject. Request is currently not rejected");
        if (domain.getResolvedTime().isBefore(Instant.now().minus(5, ChronoUnit.MINUTES)))
            throw new DomainArgumentException("Request cannot change status 5 minutes of rejection.");
    }

    public static void isValidToCancel(RequestDomain domain) {
        mustNotBeFinalized(domain, "cancel");
        if (domain.getRequestStatus() == RequestStatus.IN_PROGRESS)
            throw new DomainArgumentException("The request is already approved and cannot be cancelled.");
    }

    private static void mustNotBeFinalized(RequestDomain domain, String context) {
        if (domain.getRequestStatus() == RequestStatus.REJECTED)
            throw new DomainArgumentException("Request cannot " + context + ": rejected at " + domain.getResolvedTime());
        if (domain.getRequestStatus() == RequestStatus.CANCELLED)
            throw new DomainArgumentException("Request cannot " + context + ": cancelled at " + domain.getResolvedTime());
        if (domain.getRequestStatus() == RequestStatus.COMPLETED)
            throw new DomainArgumentException("Request cannot " + context + ": already completed");
    }


    public static void stateRejectionReason(RequestDomain domain, String message) {
        if (message == null || message.trim().isEmpty()) throw new DomainArgumentException("Message cannot be empty");
        domain.setRejectionMessage(message);
        domain.setResolvedTime(Instant.now());
    }
}
