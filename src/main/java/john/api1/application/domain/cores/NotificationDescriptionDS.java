package john.api1.application.domain.cores;

import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class NotificationDescriptionDS {
        private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a").withZone(SYSTEM_ZONE);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM d, yyyy").withZone(SYSTEM_ZONE);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a").withZone(SYSTEM_ZONE);

    public static String description(NotificationContext context) {
        return switch (context.notificationType()) {
            case PHOTO_REQUEST_COMPLETED, VIDEO_REQUEST_COMPLETED -> descriptionMediaRequestCompleted(context);
            case GROOMING_REQUEST_IN_PROGRESS -> descriptionGroomingProgress(context);
            case GROOMING_REQUEST_COMPLETED -> descriptionGroomingCompleted(context);
            case EXTENSION_REQUEST_COMPLETED -> descriptionExtensionApproved(context);
            case ADDITIONAL_CHARGES -> descriptionAdditionalCharges(context);
            case PICKUP_REMINDER -> descriptionBoardingPickup(context);
            case PHOTO_REQUEST_REJECTED,
                    VIDEO_REQUEST_REJECTED,
                    GROOMING_REQUEST_REJECTED,
                    EXTENSION_REQUEST_REJECTED -> descriptionRejected(context);

            default -> throw new DomainArgumentException("Invalid notification type");
        };
    }

    private static String descriptionMediaRequestCompleted(NotificationContext context) {
        RequestType requestType = context.requestType();
        String petName = context.petName();

        String request = switch (requestType) {
            case PHOTO_REQUEST -> "photo";
            case VIDEO_REQUEST -> "video";
            default -> throw new DomainArgumentException("Invalid request type for media description notification");
        };
        String pluralSuffix = requestType == RequestType.PHOTO_REQUEST ? "s" : "";
        return String.format("Your %s request for %s has been completed. You can view the %s%s now.", request, petName, request, pluralSuffix);
    }

    private static String descriptionGroomingProgress(NotificationContext context) {
        return String.format("Your grooming request for %s is now being processed. You will be notified once it is completed.", context.petName());
    }

    private static String descriptionGroomingCompleted(NotificationContext context) {
        return String.format("Your grooming request for %s has been completed.", context.petName());
    }

    private static String descriptionExtensionApproved(NotificationContext context) {
        String formattedDate = DATE_TIME_FORMAT.format(context.newDuration());
        return String.format("Your extension request for %s has been approved. New boarding time: %s.", context.petName(), formattedDate);
    }

    private static String descriptionAdditionalCharges(NotificationContext context) {
        RequestType requestType = context.requestType();
        String petName = context.petName();
        double charges = context.charges();

        String request = switch (requestType) {
            case BOARDING_EXTENSION -> "boarding extension";
            case GROOMING_SERVICE -> "grooming service";
            default -> throw new DomainArgumentException("Invalid request type for service description notification");
        };
        return String.format("An additional charge of ₱%d has been added for %s's %s. Payment will be collected during pickup.", (int) charges, petName, request);
    }

    private static String descriptionBoardingPickup(NotificationContext context) {
        BoardingType boardingType = context.boardingType();
        String petName = context.petName();
        Instant checkoutTime = context.checkoutTime();

        if (boardingType == BoardingType.DAYCARE) {
            String formattedTime = TIME_FORMAT.format(checkoutTime);
            return String.format("%s's daycare boarding ends today at %s. Kindly ensure timely pickup.", petName, formattedTime);
        } else if (boardingType == BoardingType.LONG_STAY) {
            String formattedDate = DATE_FORMAT.format(checkoutTime);
            return String.format("%s's long-stay boarding ends on %s. Kindly arrange for pickup accordingly.", petName, formattedDate);
        } else {
            throw new DomainArgumentException("Invalid boarding type for pickup reminder.");
        }
    }

    private static String descriptionRejected(NotificationContext context) {
        String petName = context.petName();
        return switch (context.notificationType()) {
            case PHOTO_REQUEST_REJECTED -> String.format("Your photo request for %s has been rejected.", petName);
            case VIDEO_REQUEST_REJECTED -> String.format("Your video request for %s has been rejected.", petName);
            case GROOMING_REQUEST_REJECTED -> String.format("Your grooming request for %s has been rejected.", petName);
            case EXTENSION_REQUEST_REJECTED ->
                    String.format("Your extension request for %s has been rejected.", petName);
            default -> throw new DomainArgumentException("Invalid rejected notification type for description");
        };
    }

}
