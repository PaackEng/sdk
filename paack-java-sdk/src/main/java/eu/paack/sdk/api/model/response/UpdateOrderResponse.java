package eu.paack.sdk.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderResponse {
    private String externalId;

    @JsonProperty("success")
    public void unpackExternalIdFromSuccess(Map<String, String> success) {
        externalId = success.get("externalId");
    }
}
