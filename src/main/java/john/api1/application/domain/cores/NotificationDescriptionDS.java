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


    // Media
    public static String descriptionMediaRequestCompleted(RequestType requestType, String petName) {
        String request = switch (requestType) {
            case RequestType.PHOTO_REQUEST -> "photo";
            case RequestType.VIDEO_REQUEST -> "video";
            default -> throw new DomainArgumentException("Invalid request type for media description notification");
        };

        String pluralSuffix = requestType == RequestType.PHOTO_REQUEST ? "s" : "";
        return String.format("Your %s request for %s has been completed. You can view the %s%s now.", request, petName, request, pluralSuffix);
    }

    // Grooming
    public static String descriptionGroomingProgress(String petName) {
        return String.format("Your grooming request for %s is now being processed. You will be notified once it is completed.", petName);
    }

    public static String descriptionGroomingCompleted(String petName) {
        return String.format("Your grooming request for %s has been completed.", petName);
    }

    // Extension
    public static String descriptionExtensionApproved(String petName, Instant newDuration) {
        String formattedDate = DATE_TIME_FORMAT.format(newDuration);
        return String.format("Your extension request for %s has been approved. New boarding time: %s.", petName, formattedDate);
    }

    // New pricing
    public static String descriptionAdditionalCharges(RequestType requestType, String petName, double charges) {
        String request = switch (requestType) {
            case RequestType.BOARDING_EXTENSION -> "boarding extension";
            case RequestType.GROOMING_SERVICE -> "grooming service";
            default -> throw new DomainArgumentException("Invalid request type for service description notification");
        };
        return String.format("An additional charge of ₱%d has been added for %s's %s. Payment will be collected during pickup.", (int) charges, petName, request);
    }

    // Boarding pickup reminder
    public static String descriptionBoardingPickup(BoardingType boardingType, String petName, Instant checkoutTime) {
        if (boardingType.equals(BoardingType.DAYCARE)) {
            String formattedTime = TIME_FORMAT.format(checkoutTime);
            return String.format("%s's daycare boarding ends today at %s. Kindly ensure timely pickup.", petName, formattedTime);
        }

        if (boardingType.equals(BoardingType.LONG_STAY)) {
            String formattedDate = DATE_FORMAT.format(checkoutTime);
            return String.format("%s's long-stay boarding ends on %s. Kindly arrange for pickup accordingly.", petName, formattedDate);
        }

        throw new DomainArgumentException("Invalid boarding type for pickup reminder.");
    }
}
