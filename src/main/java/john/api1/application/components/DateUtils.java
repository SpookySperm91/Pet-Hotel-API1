package john.api1.application.components;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, yyyy") // "July 5, 2025"
                    .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter FORMATTER_TIME =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH)
                    .withZone(ZoneId.systemDefault());

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();



    public static String formatInstant(Instant instant) {
        return FORMATTER.format(instant);
    }

    public static String formatInstantWithTime(Instant instant) {
        return FORMATTER_TIME.format(instant);
    }

    public static boolean isAfter(Instant instant) {
        LocalDate date = toLocalDate(instant);
        LocalDate today = LocalDate.now(DEFAULT_ZONE);
        return date.isBefore(today);
    }

    public static boolean isBeforeToday(Instant instant) {
        LocalDate date = toLocalDate(instant);
        LocalDate today = LocalDate.now(DEFAULT_ZONE);
        return date.isBefore(today);
    }

    public static boolean isToday(Instant instant) {
        LocalDate date = toLocalDate(instant);
        LocalDate today = LocalDate.now(DEFAULT_ZONE);
        return date.isEqual(today);
    }

    public static LocalDate toLocalDate(Instant instant) {
        return instant.atZone(DEFAULT_ZONE).toLocalDate();
    }

}
