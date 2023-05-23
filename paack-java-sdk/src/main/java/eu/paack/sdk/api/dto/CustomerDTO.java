package eu.paack.sdk.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerDTO {
    private AddressDTO address;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String email;
    private String phone;
    private String type;
    @JsonProperty("customer_details")
    private List<ExtraDetailDTO> customerDetails;
    private String language;
    @JsonProperty("has_gdpr_consent")
    private Boolean hasGdprConsent;
}
