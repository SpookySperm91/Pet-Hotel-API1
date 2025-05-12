package john.api1.application.components.enums;

import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    // request photo
    PHOTO_REQUEST_COMPLETED("PHOTO_REQUEST_COMPLETED", "Photo Request Completed"),
    PHOTO_REQUEST_REJECTED("PHOTO_REQUEST_REJECTED", "Photo Request Rejected"),
    // request video
    VIDEO_REQUEST_COMPLETED("VIDEO_REQUEST_COMPLETED", "Video Request Completed"),
    VIDEO_REQUEST_REJECTED("VIDEO_REQUEST_REJECTED", "Video Request Rejected"),
    // extension
    EXTENSION_REQUEST_IN_PROGRESS("EXTENSION_REQUEST_IN_PROGRESS", "Extension Request In Progress"),
    EXTENSION_REQUEST_COMPLETED("EXTENSION_REQUEST_COMPLETED", "Extension Request Completed"),
    EXTENSION_REQUEST_REJECTED("EXTENSION_REQUEST_REJECTED", "Extension Request Rejected"),
    // grooming
    GROOMING_REQUEST_IN_PROGRESS("GROOMING_REQUEST_IN_PROGRESS", "Grooming Request In Progress"),
    GROOMING_REQUEST_COMPLETED("GROOMING_REQUEST_COMPLETED", "Grooming Request Completed"),
    GROOMING_REQUEST_REJECTED("GROOMING_REQUEST_REJECTED", "Grooming Request Rejected"),
    // reminder
    ADDITIONAL_CHARGES("ADDITIONAL_CHARGES", "Additional Charges Added"),
    PICKUP_REMINDER("PICKUP_REMINDER", "Boarding Pickup Reminder");

    private final String notificationType;
    private final String notificationTypeDto;

    public static NotificationType fromString(String type) {
        for (NotificationType t : NotificationType.values()) {
            if (t.notificationType.equals(type)) {
                return t;
            }
        }
        throw new DomainArgumentException("Invalid NotificationType: " + type);
    }

    public static NotificationType check(RequestType requestType, RequestStatus status) {
        return switch (requestType) {
            case PHOTO_REQUEST -> switch (status) {
                case COMPLETED -> PHOTO_REQUEST_COMPLETED;
                case REJECTED -> PHOTO_REQUEST_REJECTED;
                default -> throw new DomainArgumentException("Unsupported status for PHOTO_REQUEST: " + status);
            };
            case VIDEO_REQUEST -> switch (status) {
                case COMPLETED -> VIDEO_REQUEST_COMPLETED;
                case REJECTED -> VIDEO_REQUEST_REJECTED;
                default -> throw new DomainArgumentException("Unsupported status for VIDEO_REQUEST: " + status);
            };
            case BOARDING_EXTENSION -> switch (status) {
                case IN_PROGRESS -> EXTENSION_REQUEST_IN_PROGRESS;
                case COMPLETED -> EXTENSION_REQUEST_COMPLETED;
                case REJECTED -> EXTENSION_REQUEST_REJECTED;
                default -> throw new DomainArgumentException("Unsupported status for EXTENSION: " + status);
            };
            case GROOMING_SERVICE -> switch (status) {
                case IN_PROGRESS -> GROOMING_REQUEST_IN_PROGRESS;
                case COMPLETED -> GROOMING_REQUEST_COMPLETED;
                case REJECTED -> GROOMING_REQUEST_REJECTED;
                default -> throw new DomainArgumentException("Unsupported status for GROOMING: " + status);
            };
            default -> throw new DomainArgumentException("Unsupported RequestType: " + requestType);
        };
    }


}
