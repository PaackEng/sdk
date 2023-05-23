package eu.paack.sdk.api.validator;

import eu.paack.sdk.model.Label;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.model.Parcel;
import lombok.Builder;

import java.util.Optional;

public class LabelValidator implements PaackValidator<Label> {

    @Builder.Default
    private ParcelValidator parcelValidator = new ParcelValidator();

    @Override
    public Optional<Error> checkForErrors(Label request) {
        if (request == null) {
            return createError("001", "Label", "Request must not be null");
        }

        if (request.getCustomer() == null) {
            return createError("001", "Customer", "Customer must not be null");
        }

        if (isBlank(request.getCustomer().getFirstName())) {
            return createError("001", "FirstName", "FirstName cannot be empty");
        }

        if (isBlank(request.getCustomer().getLastName())) {
            return createError("001", "LastName", "LastName cannot be empty");
        }

        int firstNameLength = request.getCustomer().getFirstName().length();
        int lastNameLength = request.getCustomer().getLastName().length();

        if (request.getTemplateId() == 1 || request.getTemplateId() == 2) {
            if (firstNameLength + lastNameLength > 29) {
                createError("002", "LastName", "First name and last name combined must have a maximum of 23 characters");
            }
        } else if (firstNameLength + lastNameLength > 45) {
            createError("002", "LastName", "First name and last name combined must have a maximum of 45 characters");
        }

        if (lastNameLength > 23) {
            return createError("002", "LastName", "Last name must have a maximum of 23 characters");
        }

        if (request.getDeliveryAddress() == null) {
            createError("001", "DeliveryAddress", "Delivery Address must not be null");
        }

        if (isBlank(request.getDeliveryAddress().getCity())) {
            createError("001", "City", "City cannot be empty");
        }

        int cityLength = request.getDeliveryAddress().getCity().length();

        if (cityLength > 58) {
            createError("002", "City", "City must have a maximum of 58 characters");
        }

        if (request.getDeliveryAddress().getCountry() == null) {
            createError("001", "Country", "Country cannot be empty");
        }

        if (isBlank(request.getDeliveryAddress().getLine1())) {
            createError("001", "City", "Line1 cannot be empty");
        }

        int line1Length = request.getDeliveryAddress().getLine1().length();

        if (!isBlank(request.getDeliveryAddress().getLine2())) {
            int line2Length = request.getDeliveryAddress().getLine2().length();

            if (line1Length + line2Length > 60) {
                createError("002", "Line1", "Line 1 and Line 2 combined must have a maximum of 60 characters");
            }

            if (line2Length > 30) {
                createError("002", "Line2", "Line 2 must have a maximum of 30 characters");
            }
        }

        if (isBlank(request.getDeliveryAddress().getPostCode())) {
            createError("001", "DeliveryAddress.PostCode", "PostCode cannot be empty");
        }

        int postCodeLength = request.getDeliveryAddress().getPostCode().length();

        if (postCodeLength < 4 || postCodeLength > 30) {
            createError("002", "PostCode", "PostCode length must be equal or greater then 4 and equal or less then 30 characters");
        }

        if (request.getExpectedDeliveryTs() == null) {
            createError("001", "ExpectedDeliveryTs", "ExpectedDeliveryTs must not be null");
        }

        if (request.getExpectedDeliveryTs().getStart() == null) {
            createError("001", "Start", "Start must not be null");
        }

        if (request.getExpectedDeliveryTs().getEnd() == null) {
            createError("001", "End", "End must not be null");
        }

        if (isBlank(request.getExternalId())) {
            createError("001", "ExternalId", "ExternalId cannot be empty");
        }

        if (request.getParcels() == null || request.getParcels().size() == 0) {
            createError("001", "Parcels", "Parcels cannot be empty");
        }

        for (Parcel parcel : request.getParcels()) {
            Optional<Error> error = parcelValidator.checkForErrors(parcel);
            if (error.isPresent()) {
                return error;
            }
        }

        if (request.getPickupAddress() == null) {
            createError("001", "PickupAddress", "Pickup Address must not be null");
        }

        if (isBlank(request.getPickupAddress().getCity())) {
            createError("001", "PickupAddress.City", "PickupAddress.City cannot be empty");
        }

        int pickupAddressCityLength = request.getPickupAddress().getCity().length();

        if (pickupAddressCityLength > 43) {
            createError("002", "PickupAddress.City", "PickupAddress.City must have a maximum of 43 characters");
        }

        if (request.getPickupAddress().getCountry() == null) {
            createError("001", "PickupAddress.Country", "PickupAddress.Country cannot be empty");
        }

        if (isBlank(request.getPickupAddress().getLine1())) {
            createError("001", "PickupAddress.City", "PickupAddress.Line1 cannot be empty");
        }

        int pickupAddressLine1Length = request.getPickupAddress().getLine1().length();

        if (!isBlank(request.getPickupAddress().getLine2())) {
            int pickupAddressLine2Length = request.getPickupAddress().getLine2().length();

            if (pickupAddressLine1Length + pickupAddressLine2Length > 43) {
                createError("002", "PickupAddress.Line1", "PickupAddress.Line 1 and PickupAddress.Line 2 combined must have a maximum of 43 characters");
            }

            if (pickupAddressLine2Length > 30) {
                createError("002", "PickupAddress.Line2", "PickupAddress.Line 2 must have a maximum of 30 characters");
            }
        }

        if (isBlank(request.getPickupAddress().getPostCode())) {
            createError("001", "PickupAddress.PostCode", "PickupAddress.PostCode cannot be empty");
        }

        int pickupAddressPostCodeLength = request.getPickupAddress().getPostCode().length();

        if (pickupAddressPostCodeLength < 4 || postCodeLength > 10) {
            createError("002", "PickupAddress.PostCode", "PickupAddress.PostCode length must be equal or greater then 4 and equal or less then 10 characters");
        }

        if (isBlank(request.getServiceType())) {
            createError("001", "ServiceType", "ServiceType cannot be empty");
        }

        return Optional.empty();
    }
}
