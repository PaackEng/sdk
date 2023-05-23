package eu.paack.sdk.utils;

import eu.paack.sdk.api.dto.TimeSlotDTO;
import eu.paack.sdk.api.dto.TimeSlotItemDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class TimeUtils {

    public static final String PAACK_DATE_DEFAULT_PATTERN = "yyyy-MM-dd";

    public static final String PAACK_TIME_DEFAULT_PATTERN = "HH:mm:ss";

    public static final String PAACK_STATUS_DATE_PATTERN = "yyyy-MM-ddTHH:mm:ssZ";

    public static final DateTimeFormatter PAACK_DATE_FORMAT = DateTimeFormatter.ofPattern(PAACK_DATE_DEFAULT_PATTERN);
    public static final DateTimeFormatter PAACK_TIME_FORMAT = DateTimeFormatter.ofPattern(PAACK_TIME_DEFAULT_PATTERN);

    private TimeUtils(){}

    public static TimeSlotDTO toTimeSlot(LocalDateTime start, LocalDateTime end) {
        return TimeSlotDTO.builder()
                .start(new TimeSlotItemDTO(start.format(PAACK_DATE_FORMAT), start.format(PAACK_TIME_FORMAT)))
                .end(new TimeSlotItemDTO(end.format(PAACK_DATE_FORMAT), end.format(PAACK_TIME_FORMAT)))
                .build();
    }


    public static LocalDateTime convertToUTC(LocalDateTime dateTime) {
        ZonedDateTime date = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
        return date.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public static LocalDateTime convertFromUTC(LocalDateTime dateTime) {
        return ZonedDateTime.of(dateTime, ZoneId.of("UTC"))
                .toOffsetDateTime().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }


    public static boolean isValidFormat(String format, String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        try {
            LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
            String expected = dateTime.format(formatter);
            return expected.equals(value);
        } catch (DateTimeParseException e) {
            log.error("Invalid date on validation", e.getMessage());
            return false;
        }
    }
}
