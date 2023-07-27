package eu.paack.sdk.config.remote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DomainConfig {

    Map<String,String> order;
    Map<String,String> trackingPull;
    Map<String,String> coverage;
    Map<String,String> pod;
    Map<String,String> label;
}
