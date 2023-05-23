package eu.paack.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {

    private Country country;
    private String county;
    private String city;
    private String line1;
    private String line2;
    private String postCode;
    private String instructions;
}
