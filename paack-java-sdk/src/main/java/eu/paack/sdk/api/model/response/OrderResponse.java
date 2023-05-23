package eu.paack.sdk.api.model.response;

import eu.paack.sdk.model.Order;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderResponse {

    private Order order;
    private String status;
    private String labels;
}
