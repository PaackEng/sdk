package eu.paack.sdk.config.remote;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Map;
@Data
@ToString
public class ResourcesConfig {

    String order;
    @JsonProperty("tracking_pull")
    Map<String, String> trackingPull;
    String coverage;
    String label;
    String pod;
}
