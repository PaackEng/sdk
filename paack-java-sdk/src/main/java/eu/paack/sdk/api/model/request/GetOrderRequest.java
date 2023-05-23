package eu.paack.sdk.api.model.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetOrderRequest {

    String externalId;
    String include;
    String labelFormat;

}
