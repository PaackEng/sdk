package eu.paack.sdk.api.model.response;

import eu.paack.sdk.model.ProofOfDelivery;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProofOfDeliveryResponse {
    private List<ProofOfDelivery> verifications;
}
