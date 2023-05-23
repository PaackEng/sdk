package eu.paack.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Customer {

    private Address address;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private List<ExtraDetail> customerDetails;
    private Language language;
    private Boolean hasGdprConsent;
    private CustomerType customerType;
}
