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
public class FileConfig {
    private Map<String,String> mapping;
    private String filenames;
    private String delimiter;
    private String filetype;
}
