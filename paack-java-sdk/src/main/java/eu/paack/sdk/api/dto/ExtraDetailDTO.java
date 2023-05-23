package eu.paack.sdk.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExtraDetailDTO {

    private String name;
    private String type;
    private String value;
}
