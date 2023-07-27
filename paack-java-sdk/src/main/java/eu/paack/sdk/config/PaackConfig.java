package eu.paack.sdk.config;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PaackConfig {
    private AuthConfig oAuth;
    private String configServer;
    private int connectionRequestTimeout;
    private int connectTimeout;
    private int socketTimeout;
    private int maxRetries;
    private int retryIntervalInMillis;
    private double retryIntervalMultiplicationFactor;

    private Map<String, String> domains = new HashMap<>();
    private Map<String, String> audiences = new HashMap<>();
    private Map<String, Object> resources = new HashMap<>();

    public String getDomain(String name) {
        return domains.get(name);
    }

    public String getAudience(String name) {
        return audiences.get(name);
    }

    public String getResource(String resource) {
        return (String) resources.get(resource);
    }

    public String getResource(String resource, String group) {
        Map<String, String> m = (Map<String, String>) resources.get(group);
        return m.get(resource);
    }
}
