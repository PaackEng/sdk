package eu.paack.sdk.api.validator;

import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.model.Parcel;

import java.util.Optional;

public class ParcelValidator implements PaackValidator<Parcel> {
    @Override
    public Optional<Error> checkForErrors(Parcel parcel) {
        if (isBlank(parcel.getBarcode())) {
            return createError("001", "Barcode", "Barcode must not be null");
        }

        return Optional.empty();
    }
}
