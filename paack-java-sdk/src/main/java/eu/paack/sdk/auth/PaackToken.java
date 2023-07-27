package eu.paack.sdk.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaackToken {
    @JsonProperty("access_token")
    String accessToken;
    @JsonProperty("expires_in")
    long expiresIn;
    @JsonProperty("token_type")
    String tokenType;
}
