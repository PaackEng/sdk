package eu.paack.sdk.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaackConfigsResponse {

    private String status;
    private PaackConfigResponse result;
}
