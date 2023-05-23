package eu.paack.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RetailerLocation {

    private RetailerLocationAddress address;
    private String alias;
    @JsonProperty("retailer_id")
    private String retailerId;
    private String id;
    @JsonProperty("location_name")
    private String locationName;
    @JsonProperty("retailer_name")
    private String retailerName;
    private String type;
}
