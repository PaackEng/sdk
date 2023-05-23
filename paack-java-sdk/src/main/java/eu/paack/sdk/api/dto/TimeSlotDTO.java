package eu.paack.sdk.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TimeSlotDTO {

    TimeSlotItemDTO start;
    TimeSlotItemDTO end;
}
