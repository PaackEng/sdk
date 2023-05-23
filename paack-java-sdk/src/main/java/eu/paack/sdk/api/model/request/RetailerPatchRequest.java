package eu.paack.sdk.api.model.request;

import eu.paack.sdk.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailerPatchRequest {
    private Address address;
    private String alias;
    private String DcID;
    private String type;
}

