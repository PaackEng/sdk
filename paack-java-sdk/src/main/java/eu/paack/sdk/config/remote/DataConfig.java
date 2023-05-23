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
public class DataConfig {

    Map<String, Map<String,String>> domain;
    Map<String, Map<String,String>> audiences;
    Map<String, Object> resources;
}
