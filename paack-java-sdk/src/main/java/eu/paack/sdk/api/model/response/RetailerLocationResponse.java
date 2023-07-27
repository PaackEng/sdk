package eu.paack.sdk.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailerLocationResponse {

    private String retailerLocationId;

    @JsonProperty("success")
    private void unpackRetailerId(Map<String, String> success) {
        this.retailerLocationId = (String)success.get("retailer_location_id");
    }

}
