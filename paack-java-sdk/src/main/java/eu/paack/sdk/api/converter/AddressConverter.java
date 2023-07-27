package eu.paack.sdk.api.converter;

import eu.paack.sdk.api.dto.AddressDTO;
import eu.paack.sdk.model.Address;
import eu.paack.sdk.model.Country;

public class AddressConverter {

    public static AddressDTO toDTO(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDTO.builder()
                .country(address.getCountry() == null ? null : address.getCountry().getCountryCode())
                .county(address.getCounty())
                .city(address.getCity())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .postCode(address.getPostCode())
                .build();
    }

    public static Address toModel(AddressDTO addressDTO, String instructions) {
        if (addressDTO == null) {
            return null;
        }
        return Address.builder()
                .country(Country.fromString(addressDTO.getCountry()))
                .county(addressDTO.getCounty())
                .city(addressDTO.getCity())
                .line1(addressDTO.getLine1())
                .line2(addressDTO.getLine2())
                .postCode(addressDTO.getPostCode())
                .instructions(instructions)
                .build();
    }
}
