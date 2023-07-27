package eu.paack.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoverageCodes {
    @JsonProperty
    private String country;
    @JsonProperty
    private String coverage_codes;

}
