package eu.paack.sdk.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.paack.sdk.config.remote.DataConfig;
import eu.paack.sdk.config.remote.FileConfig;
import eu.paack.sdk.config.remote.MappingsConfig;
import eu.paack.sdk.config.remote.PathsConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaackConfigResponse {

    @JsonProperty("file_config")
    FileConfig fileConfig;
    @JsonProperty("data_config")
    DataConfig dataConfig;
    PathsConfig paths;
    MappingsConfig mappings;
}
