package eu.paack.sdk.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailerGetRequest {

    private String id;
    private String retailerName;
    private String locationName;
    private String country;
    private String city;
    private String postcode;
    private String alias;
    private String type;
    private String retailerId;
}
