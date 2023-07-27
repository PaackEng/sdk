package eu.paack.sdk.api.validator;

import eu.paack.sdk.api.model.request.GetOrderRequest;
import eu.paack.sdk.api.model.response.Error;

import java.util.Optional;

public class GetOrderRequestValidator implements PaackValidator<GetOrderRequest> {

    @Override
    public Optional<Error> checkForErrors(GetOrderRequest request) {
        if (request == null) {
            return createError("001", "GetOrderRequest", "Request must not be null");
        }

        if (isBlank(request.getExternalId())) {
            return createError("001", "ExternalId", "ExternalID must not be null");
        }

        if ((!isBlank(request.getInclude()) && isBlank(request.getLabelFormat()))
                || (isBlank(request.getInclude()) && !isBlank(request.getLabelFormat()))) {
            return createError("001", "Include", "Include and LabelFormat both must be null or not null");
        }

        return Optional.empty();
    }
}
