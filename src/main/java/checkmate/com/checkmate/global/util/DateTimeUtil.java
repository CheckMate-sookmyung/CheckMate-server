package checkmate.com.checkmate.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static LocalDateTime parseEventDateTime(String eventDate, String eventTime) {
        String dateTimeString = eventDate + " " + eventTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(dateTimeString, formatter);
    }
}