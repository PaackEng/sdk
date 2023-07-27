package eu.paack.sdk.api.model.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusesRequest {
    List<String> externalIds;
    Integer count;
    LocalDateTime start;
    LocalDateTime end;
    String before;
    String after;

}
