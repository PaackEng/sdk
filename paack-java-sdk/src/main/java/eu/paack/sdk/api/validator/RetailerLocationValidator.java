package eu.paack.sdk.api.validator;

import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.model.RetailerLocation;

import java.util.Optional;

public class RetailerLocationValidator implements PaackValidator<RetailerLocation> {
    @Override
    public Optional<Error> checkForErrors(RetailerLocation request) {
        if (request == null) {
            return Optional.empty();
        }

        if (request.getAddress() == null) {
            return createError("001", "Address", "Address cannot be empty");
        }

        if (isBlank(request.getRetailerName())) {
            return createError("001", "RetailerName", "RetailerName cannot be empty");
        }

        if (isBlank(request.getLocationName())) {
            return createError("001", "LocationName", "LocationName cannot be empty");
        }
        return Optional.empty();
    }
}
