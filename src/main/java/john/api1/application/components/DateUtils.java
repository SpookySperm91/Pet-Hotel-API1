package john.api1.application.components;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, yyyy") // "July 5, 2025"
                    .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter FORMATTER_TIME =
            DateTimeFormatter.ofPattern("MMMM d, yyyy, hh:mm a")
                    .withZone(ZoneId.systemDefault());

    public static String formatInstant(Instant instant) {
        return FORMATTER.format(instant);
    }

    public static String formatInstantWithTime(Instant instant) {
        return FORMATTER_TIME.format(instant);
    }

}
