package eu.paack.sdk.api.model.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderCreateSuccessResponse {
   private CreateOrderSuccess success;
}
