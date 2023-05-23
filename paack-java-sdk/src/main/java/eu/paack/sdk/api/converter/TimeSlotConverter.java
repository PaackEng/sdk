package eu.paack.sdk.api.converter;

import eu.paack.sdk.api.dto.TimeSlotDTO;
import eu.paack.sdk.api.dto.TimeSlotItemDTO;
import eu.paack.sdk.model.TimeSlot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeSlotConverter {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static TimeSlotDTO toDTO(TimeSlot timeSlot) {

        if (timeSlot == null) {
            return null;
        }

        return TimeSlotDTO.builder()
                .start(convertTimeDate(timeSlot.getStart()))
                .end(convertTimeDate(timeSlot.getEnd()))
                .build();
    }

    public static TimeSlot toModel(TimeSlotDTO timeSlotDTO) {

        if (timeSlotDTO == null) {
            return null;
        }

        LocalDateTime startTime = LocalDateTime.parse(timeSlotDTO.getStart().getDate() + " " + timeSlotDTO.getStart().getTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(timeSlotDTO.getEnd().getDate() + " " + timeSlotDTO.getEnd().getTime(), formatter);

        return TimeSlot.builder()
                .end(endTime)
                .start(startTime)
                .build();
    }

    private static TimeSlotItemDTO convertTimeDate(LocalDateTime dateTime) {

        return TimeSlotItemDTO.builder()
                .date(dateTime.format(dateFormatter))
                .time(dateTime.format(timeFormatter))
                .build();
    }


}
