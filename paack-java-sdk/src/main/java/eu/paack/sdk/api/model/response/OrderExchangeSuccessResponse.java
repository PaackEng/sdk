package eu.paack.sdk.api.model.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderExchangeSuccessResponse {

    private CreateOrderSuccess directOrder;
    private CreateOrderSuccess reverseOrder;
}
