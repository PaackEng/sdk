package eu.paack.sdk.api.validator;

import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.model.ExtraDetail;
import eu.paack.sdk.model.Order;
import eu.paack.sdk.model.Parcel;
import lombok.Builder;

import java.util.Optional;

public class OrderValidator implements PaackValidator<Order> {

    @Builder.Default
    private AddressValidator addressValidator = new AddressValidator();
    @Builder.Default
    private CustomerValidator customerValidator = new CustomerValidator();
    @Builder.Default
    private ParcelValidator parcelValidator = new ParcelValidator();

    @Override
    public Optional<Error> checkForErrors(Order order) {
        Optional<Error> error;

        if (order == null) {
            return createError("001", "request", "Request cannot be null");
        }

        if (isBlank(order.getExternalId())) {
            return createError("001", "ExternalId", "ExternalId must not be null");
        }

        if (order.getCustomer() == null) {
            return createError("001", "Customer", "Customer must not be null");
        }

        error = customerValidator.checkForErrors(order.getCustomer());
        if (error.isPresent()) {
            return error;
        }

        if (order.getDeliveryAddress() == null) {
            return createError("001", "DeliveryAddress", "Delivery address must not be null.");
        }

        error = addressValidator.checkForErrors(order.getDeliveryAddress());
        if (error.isPresent()) {
            return error;
        }

        if (order.getExpectedDeliveryTs() == null) {
            return createError("001", "ExpectedDeliveryTs", "Expected Delivery Ts must not be null");
        }

        if (order.getExpectedDeliveryTs().getStart() == null) {
            createError("001", "ExpectedDeliveryTs.Start", "Start must not be null");
        }

        if (order.getExpectedDeliveryTs().getEnd() == null) {
            createError("001", "ExpectedDeliveryTs.End", "End must not be null");
        }

        if (isBlank(order.getServiceType())) {
            return createError("001", "ServiceType", "Service Type must not be null");
        }

        if (order.getParcels() == null) {
            return createError("001", "Parcels", "Parcels must not be null");
        }

        for (Parcel parcel : order.getParcels()) {
            error = parcelValidator.checkForErrors(parcel);
            if (error.isPresent()) {
                return error;
            }
        }

        if (order.getPickupAddress() == null) {
            return createError("001", "PickupAddress", "Pickup address must not be null.");
        }

        error = addressValidator.checkForErrors(order.getPickupAddress());
        if (error.isPresent()) {
            return error;
        }

       /* if (order.getExpectedPickUpTs() == null) {
            return createError("001", "ExpectedPickUpTs", "Expected PickUp TS must not be null");
        }

        if (order.getExpectedPickUpTs().getStart() == null) {
            createError("001", "ExpectedPickUpTs.Start", "Expected PickUp Ts Start must not be null");
        }

        if (order.getExpectedPickUpTs().getEnd() == null) {
            createError("001", "ExpectedPickUpTs.End", "Expected PickUp Ts End must not be null");
        }*/

        if (order.getOrderDetails() != null) {
            for (ExtraDetail orderDetail : order.getOrderDetails()) {
                if (orderDetail.getType() == null
                        || orderDetail.getName() == null
                        || orderDetail.getValue() == null) {
                    return createError("001", "Order Detail", "Type,name and value in order details must not be null");
                }
            }
        }
        return Optional.empty();
    }
}
