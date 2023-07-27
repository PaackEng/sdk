package eu.paack.sdk.config.remote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PathsConfig {

    String pending;
    String errors;
    String processed;
}
