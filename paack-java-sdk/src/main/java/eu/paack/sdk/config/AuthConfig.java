package eu.paack.sdk.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthConfig {

    private String audience;
    private String clientId;
    private String clientSecret;
    private String grantType;
    private String issuerUrl;

}
