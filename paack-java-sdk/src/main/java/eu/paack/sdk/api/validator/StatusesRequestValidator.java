package eu.paack.sdk.api.validator;

import eu.paack.sdk.api.model.request.StatusesRequest;
import eu.paack.sdk.api.model.response.Error;

import java.util.Optional;

public class StatusesRequestValidator implements PaackValidator<StatusesRequest> {

    @Override
    public Optional<Error> checkForErrors(StatusesRequest request) {
        if (request == null) {
            return createError("001", "StatusesRequest", "Request must not be null");
        }

        if (request.getCount() <= 10 && request.getCount() > 100) {
            return createError("002", "Count", "Count must be equal or greater than ten and equal or less than 100");
        }

        return Optional.empty();
    }

}
