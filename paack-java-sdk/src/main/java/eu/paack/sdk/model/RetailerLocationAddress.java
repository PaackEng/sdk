package eu.paack.sdk.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RetailerLocationAddress {
    private String country;
    private String county;
    private String city;
    private String line1;
    private String line2;
    private String post_code;
    private String instructions;
}
