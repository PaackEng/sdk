package eu.paack.sdk.api.model.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateOrderSuccess {

    private String trackingID;
    private String labels;
    private String status;
}
