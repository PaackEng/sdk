package eu.paack.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoverageZones {
    @JsonProperty
    private String country;
    @JsonProperty
    private String coverage_zone;
}
