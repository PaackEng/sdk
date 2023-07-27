package eu.paack.sdk.api.validator;

import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.model.Address;

import java.util.Optional;

public class AddressValidator implements PaackValidator<Address> {
    @Override
    public Optional<Error> checkForErrors(Address address) {
        if(address == null) {
            return createError("001", "Address", "Request must not be null");
        }

        if(address.getCountry() == null) {
            return createError("001", "Country", "Country must not be null");
        }

        if (isBlank(address.getCity())) {
            return createError("001", "City", "City field must be provided for address");
        }

        int cityLength = address.getCity().length();
        if (cityLength < 2 || cityLength > 128) {
            return createError("002", "City", "City name's length must be equal or greater then 2 and equal or less then 128 characters");
        }

        if (isBlank(address.getPostCode())) {
            return createError("001", "PostCode", "Post code field must be provided for address");
        }

        int postCodeLength = address.getPostCode().length();
        if (postCodeLength < 3 || postCodeLength > 128) {
            return createError("002", "PostCode", "Post code's length must be equal or greater then 3 and equal or less then 128 characters");
        }

        if (address.getCountry() == null) {
            return createError("001", "Country", "Country field must be provided for address");
        }

        if (isBlank(address.getLine1())) {
            return createError("001", "Line1", "Line1 field must be provided for address");
        }

        int line1Length = address.getLine1().length();
        if (line1Length < 2 || line1Length > 128) {
            return createError("002", "Line1", "Line1 length must be equal or greater then 2 and equal or less then 128 characters");
        }

        if (!isBlank(address.getLine2()) && address.getLine2().length() > 128) {
            return createError("002", "PostCode", "Line2 length must be equal or less then 128 characters");
        }

        return Optional.empty();
    }
}
