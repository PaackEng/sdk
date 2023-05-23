package eu.paack.sdk.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressDTO {

    private String country;
    private String county;
    private String city;
    private String line1;
    private String line2;
    @JsonProperty("post_code")
    private String postCode;
}
