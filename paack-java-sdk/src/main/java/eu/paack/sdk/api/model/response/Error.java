package eu.paack.sdk.api.model.response;

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
public class Error {
    private String id;
    private String code;
    private String status;
    private String source;
    private String detail;
    private String message;
}
