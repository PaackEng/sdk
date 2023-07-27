package eu.paack.sdk.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.paack.sdk.model.RetailerLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailerGetResponse {
    @JsonProperty("retailer_locations")
    private List<RetailerLocation> locations;
}
