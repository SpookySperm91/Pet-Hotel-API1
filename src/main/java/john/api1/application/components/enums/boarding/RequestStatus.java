package john.api1.application.components.enums.boarding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestStatus {
    PENDING("PENDING"),
    IN_PROGRESS("IN_PROGRESS"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED"),
    REJECTED("REJECTED");

    private final String requestStatus;

    public static RequestStatus fromString(String requestStatus) {
        if (requestStatus == null) {
            return null;
        }

        for (RequestStatus status : RequestStatus.values()) {
            if (status.getRequestStatus().equalsIgnoreCase(requestStatus)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown request status: " + requestStatus);
    }


}
