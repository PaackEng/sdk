package eu.paack.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.paack.sdk.PaackConstants;
import eu.paack.sdk.api.converter.OrderConverter;
import eu.paack.sdk.api.converter.OrderResponseConverter;
import eu.paack.sdk.api.dto.OrderDTO;
import eu.paack.sdk.api.dto.OrderResponseSuccessDTO;
import eu.paack.sdk.api.validator.*;
import eu.paack.sdk.model.*;
import eu.paack.sdk.api.model.request.GetOrderRequest;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.*;
import eu.paack.sdk.exceptions.ApiException;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Allows you to easily manage orders.
 *
 * The Order model / structure is required as a parameter for the following functions:
 * CreateWithWarehouse, CreateWithStore, UpdateOrder, UpsertOrderWithWarehouse, UpsertOrderWithStore.
 * It follows a similar structure as expected by the API but has a few improvements to make it easier to populate with values.
 *
 * Each parcel contains WeightUnits and LengthUnits. Possible values for WeightUnits are ["mg", "g", "kg"].
 * Possible values for LengthUnits are ["mm", "cm", "m"].
 * These units are used by the SDK to normalize Height, Length, Width to 'cm' and Weight to 'kg'.
 *
 * All times specified in the input values will be converted to UTC by the SDK.
 * If they are already in UTC, they will be left as it is.
 * The SDK will use the country and postcode of the delivery address to infer the Timezone of the delivery time you supplied, when converting to UTC.
 *
 * The phone number, if provided without a prefix, will be automatically prefixed by the SDK using the country of the delivery address.
 * This is needed as Paack API requires the phone number to have a prefix.
 *
 * More details can be found in the Paack API documentation: https://paack.readme.io/reference/orders
 */
@Slf4j
@SuperBuilder
@NoArgsConstructor
public class OrderApi extends PaackApi {

    @Builder.Default
    private GetOrderRequestValidator getOrderRequestValidator = new GetOrderRequestValidator();
    @Builder.Default
    private OrderValidator orderValidator = new OrderValidator();
    @Builder.Default
    private ParcelValidator parcelValidator = new ParcelValidator();
    @Builder.Default
    private AddressValidator addressValidator = new AddressValidator();

    /**
     * Create a new Order for the Warehouse Model. Enable model by default when called.
     * Applies validations specific for the Warehouse Model.
     *
     * Args:
     * payload: OrderSchema payload
     * labelFormat: LabelFormat if the label should be returned as well. If no label is needed then leave it empty
     * Returns:
     * OrderCreateSuccessResponse | Error
     * @param payload
     * @param withOrderLabel
     * @return
     */
    public PaackResponse<OrderCreateSuccessResponse, Error> createWithWarehouse(Order payload, boolean withOrderLabel) {
        return create(payload);
    }

    /**
     * Create a new Order for the Store Model. Enable model by default when called.
     * Applies validations specific for the Store Model.
     *
     * Args:
     * payload: OrderSchema payload
     * 	labelFormat: LabelFormat if the label should be returned as well. If no label is needed then leave it empty
     * Returns:
     * OrderCreateSuccessResponse | Error
     * @param payload
     * @param withOrderLabel
     * @return
     */
    public PaackResponse<OrderCreateSuccessResponse, Error> createWithStore(Order payload, boolean withOrderLabel) {
        return create(payload);
    }

    /**
     * Update a single parcel from an Order payload.
     * The function makes a request to take the existing order values and then update the parcels with the new parcel details.
     * The parcel is matched based on the barcode.
     *
     * Args:
     * parcel: Parcel data to be updated with a barcode matching existing barcodes.
     * orderId: Order's external ID
     * Returns:
     * UpdateOrderResponse | Error
     * @param orderId
     * @param parcel
     * @return
     */
    public PaackResponse<UpdateOrderResponse, Error> updateParcel(String orderId, Parcel parcel) {
        if (orderId == null || orderId.length() == 0) {
            return errorMessage("OrderId cannot be empty", "orderAPI.updateParcel.orderId", "001");
        }

        Optional<Error> error = parcelValidator.checkForErrors(parcel);
        if (error.isPresent()) {
            return errorMessage(error.get());
        }

        try {
            PaackResponse<OrderResponse, Error> orderResp = getOrder(orderId, null, null);
            if (orderResp.getData() == null || orderResp.getData().getOrder() == null) {
                if (orderResp.getError() != null && !orderResp.getError().isEmpty()) {
                    return errorMessage(orderResp.getError());
                }
                return errorMessage("Could not find an on order matching the provided order ID.", "OrderApi.updateParcel", "003");
            }
            Order order = orderResp.getData().getOrder();
            if (order.getParcels() != null) {
                List<Parcel> parcels = order.getParcels().stream()
                        .map(p -> p.getBarcode().equals(parcel.getBarcode()) ? parcel : p)
                        .collect(Collectors.toList());
                order.setParcels(parcels);

            } else {
                order.setParcels(Collections.singletonList(parcel));
            }
            return update(orderId, order);
        } catch (ApiException e) {
            log.error("Update order parcel with barcode: %s failed", e);
            return errorMessage("OrderApi.updateParcel", e.getMessage());
        }
    }

    /**
     * Bulk update parcels from an Order payload.
     * The function makes a request to take the existing order values and then update the parcels with the new parcels details.
     * Parcels where the barcode is matching will have their values updated.
     * Parcels there were in the order but are not in the new list will be removed.
     * Parcels that were not in the order before but now are added will be added to the order.
     *
     * Args:
     * parcelList: List of Parcel data to be updated.
     * orderId: Order's external ID
     * Returns:
     * UpdateOrderResponse | Error
     * @param orderId
     * @param parcels
     * @return
     */
    public PaackResponse<UpdateOrderResponse, Error> updateParcels(String orderId, List<Parcel> parcels) {
        if (orderId == null || orderId.length() == 0) {
            return errorMessage("OrderId cannot be empty", "orderAPI.updateParcels.orderId", "001");
        }

        if (parcels == null || parcels.size() == 0) {
            return errorMessage("Parcels cannot be empty", "orderAPI.updateParcels.parcels", "001");
        }

        Optional<Error> error;
        for (Parcel parcel : parcels) {
            error = parcelValidator.checkForErrors(parcel);
            if (error.isPresent()) {
                return errorMessage(error.get());
            }
        }

        try {
            PaackResponse<OrderResponse, Error> orderResp = getOrder(orderId, null, null);
            if (orderResp.getData() == null || orderResp.getData().getOrder() == null) {
                if (orderResp.getError() != null && !orderResp.getError().isEmpty()) {
                    return errorMessage(orderResp.getError());
                }
                return errorMessage("Could not find an on order matching the provided order ID.", "OrderApi.updateParcels", "003");
            }
            Order order = orderResp.getData().getOrder();
            order.setParcels(parcels);
            return update(orderId, order);
        } catch (ApiException e) {
            log.error("Update order parcels failed", e);
            return errorMessage("OrderApi.updateParcels", e.getMessage());
        }
    }

    /**
     * Update delivery address from an Order payload.
     *The function makes a request to take the existing values and then updates them with the new delivery address values from the request.
     * Args:
     * address: the new delivery address
     * orderId: Order's external ID
     * Returns:
     * UpdateOrderResponse | Error
     * @param orderId
     * @param address
     * @return
     */
    public PaackResponse<UpdateOrderResponse, Error> updateDeliveryAddress(String orderId, Address address) {
        if (orderId == null || orderId.length() == 0) {
            return errorMessage("OrderId cannot be empty", "orderAPI.updateDeliveryAddress.orderId", "001");
        }

        Optional<Error> error = addressValidator.checkForErrors(address);
        if (error.isPresent()) {
            return errorMessage(error.get());
        }

        try {
            PaackResponse<OrderResponse, Error> orderResp = getOrder(orderId, null, null);
            if (orderResp.getData() == null || orderResp.getData().getOrder() == null) {
                if (orderResp.getError() != null && !orderResp.getError().isEmpty()) {
                    return errorMessage(orderResp.getError());
                }
                return errorMessage("Could not find an on order matching the provided order ID.", "OrderApi.updateDeliveryAddress", "003");
            }
            Order order = orderResp.getData().getOrder();
            order.setDeliveryAddress(address);
            return update(orderId, order);
        } catch (ApiException e) {
            log.error("Update order address failed", e);
            return errorMessage("OrderApi.updateDeliveryAddress", e.getMessage());
        }
    }

    /**
     * Update contact details from an Order payload.
     * The function makes a request to take the existing values and then updates them with the new contact details values from the request.
     *
     * Args:
     * contactInfo: the receiver customer contact details
     * orderId: Order's external ID
     * Returns:
     * UpdateOrderResponse | ErrorResponse
     * @param orderId
     * @param mail
     * @param phone
     * @param hasGDPRConsent
     * @return
     */
    public PaackResponse<UpdateOrderResponse, Error> updateCustomerContactDetails(String orderId, String mail, String phone, boolean hasGDPRConsent) {
        if (orderId == null || orderId.length() == 0) {
            return errorMessage("OrderId cannot be empty", "orderAPI.updateCustomerContactDetails.orderId", "001");
        }
        if (mail == null || mail.length() == 0) {
            return errorMessage("Mail cannot be empty", "orderAPI.updateCustomerContactDetails.mail", "001");
        }
        if (phone == null || phone.length() == 0) {
            return errorMessage("OrderId cannot be empty", "orderAPI.updateCustomerContactDetails.orderId", "001");
        }
        try {
            PaackResponse<OrderResponse, Error> orderResp = getOrder(orderId, null, null);
            if (orderResp.getData() == null || orderResp.getData().getOrder() == null) {
                if (orderResp.getError() != null && !orderResp.getError().isEmpty()) {
                    return errorMessage(orderResp.getError());
                }
                return errorMessage("Could not find an on order matching the provided order ID.", "OrderApi.updateCustomerContactDetails", "003");
            }
            Order order = orderResp.getData().getOrder();
            if (order.getCustomer() == null) {
                log.error("Customer details update failed");
                return errorMessage("OrderApi.updateCustomerContactDetails", "Customer details are not present in the order");
            }
            order.getCustomer().setEmail(mail);
            order.getCustomer().setPhone(phone);
            order.getCustomer().setHasGdprConsent(hasGDPRConsent);
            return update(orderId, order);
        } catch (ApiException e) {
            log.error("Customer order address failed", e);
            return errorMessage("OrderApi.updateCustomerContactDetails", e.getMessage());
        }
    }

    /**
     * Update all changeable parameters from an Order payload.
     * The function makes a request to take the existing values and then updates them with the new values from the request.
     *
     * Args:
     * order: order information
     * Returns:
     * UpdateOrderResponse | Error
     * @param order
     * @return
     */
    public PaackResponse<UpdateOrderResponse, Error> updateOrder(Order order) {
        Optional<Error> error = orderValidator.checkForErrors(order);
        if (error.isPresent()) {
            return errorMessage(error.get());
        }

        try {
            return update(order.getExternalId(), order);
        } catch (ApiException e) {
            log.error("Update order failed", e);
            return errorMessage("OrderApi.updateOrder", e.getMessage());
        }
    }

    /**
     * Update all changeable parameters from a Warehouse model Order, if the order exists.
     * Otherwise, it will create a new order using the same parameters.
     *
     * Args:
     * order: order information
     * Returns:
     * OrderCreateSuccessResponse | Error
     * @param order
     * @return
     */
    public PaackResponse<OrderCreateSuccessResponse, Error> upsertOrderWithWarehouse(Order order) {
        Optional<Error> orderError = orderValidator.checkForErrors(order);
        if (orderError.isPresent()) {
            return errorMessage(orderError.get());
        }

        try {
            PaackResponse<OrderResponse, Error> orderResp = getOrder(order.getExternalId(), null, null);
            if (orderResp.getData() == null) {
                return createWithWarehouse(order, false);
            }

            PaackResponse<UpdateOrderResponse, Error> response = updateOrder(order);

            if (response.getData() == null) {
                return PaackResponse.<OrderCreateSuccessResponse, Error>builder()
                        .error(response.getError())
                        .build();
            }


            CreateOrderSuccess createOrderSuccess = CreateOrderSuccess.builder()
                    .trackingID(response.getData().getExternalId()).build();

            OrderCreateSuccessResponse orderCreateSuccessResponse = OrderCreateSuccessResponse.builder()
                    .success(createOrderSuccess).build();


            return PaackResponse.<OrderCreateSuccessResponse, Error>builder()
                    .data(orderCreateSuccessResponse)
                    .build();
        } catch (ApiException exception) {
            log.error("Upsert with warehouse failed", exception);
            return errorMessage("OrderApi.upsertOrderWithWarehouse", exception.getMessage());
        }

    }

    /**
     * Update all changeable parameters from an Order payload with Store Model if order exists.
     * Otherwise, create a new order using the same parameters.
     *
     * Args:
     * order: order information
     * Returns:
     * OrderCreateSuccessResponse | Error
     * @param order
     * @return
     */
    public PaackResponse<OrderCreateSuccessResponse, Error> upsertOrderWithStore(Order order) {
        Optional<Error> error = orderValidator.checkForErrors(order);
        if (error.isPresent()) {
            return errorMessage(error.get());
        }

        try {
            PaackResponse<OrderResponse, Error> orderResp = getOrder(order.getExternalId(), null, null);
            if (orderResp.getData() == null) {
                return createWithStore(order, false);
            }

            PaackResponse<UpdateOrderResponse, Error> response = updateOrder(order);

            if (response.getData() == null) {
                return PaackResponse.<OrderCreateSuccessResponse, Error>builder()
                        .error(response.getError())
                        .build();
            }


            CreateOrderSuccess createOrderSuccess = CreateOrderSuccess.builder()
                    .trackingID(response.getData().getExternalId()).build();

            OrderCreateSuccessResponse orderCreateSuccessResponse = OrderCreateSuccessResponse.builder()
                    .success(createOrderSuccess).build();


            return PaackResponse.<OrderCreateSuccessResponse, Error>builder()
                    .data(orderCreateSuccessResponse)
                    .build();
        } catch (ApiException exception) {
            log.error("Upsert with store failed", exception);
            return errorMessage("OrderApi.upsertOrderWithStore", exception.getMessage());
        }
    }

    /**
     * Create a new direct Order and a new reverse Order for the Warehouse Model from the exchange order information.
     * It automatically takes care to set the pickup and delivery address and timeslot correctly.
     * It manages errors if one of the 2 orders fails.
     *
     * Args:
     * request: OrderExchangeRequest
     * Returns:
     * OrderExchangeSuccessResponse | Error
     * @param request
     * @return
     */
    public PaackResponse<OrderExchangeSuccessResponse, Error> exchangeWithWarehouse(ExchangeOrder request) {

        Order directOrder = buildOrderForExchange(request, DeliveryType.DIRECT);
        Order reverseOrder = buildOrderForExchange(request, DeliveryType.REVERSE);
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        directOrder.setClusters(Collections.singletonList(uuidAsString));
        reverseOrder.setClusters(Collections.singletonList(uuidAsString));
        PaackResponse<OrderCreateSuccessResponse, Error> directOrderResponse = create(directOrder);
        PaackResponse<OrderCreateSuccessResponse, Error> reverseOrderResponse = create(reverseOrder);

        if (directOrderResponse.getError() != null) {
            return PaackResponse.<OrderExchangeSuccessResponse, Error>builder()
                    .error(directOrderResponse.getError())
                    .build();
        }

        if (reverseOrderResponse.getError() != null) {
            return PaackResponse.<OrderExchangeSuccessResponse, Error>builder()
                    .error(reverseOrderResponse.getError())
                    .build();
        }
        OrderExchangeSuccessResponse response = null;

        if (directOrderResponse.getData() != null && reverseOrderResponse.getData() != null) {
            response = OrderExchangeSuccessResponse.builder()
                    .directOrder(directOrderResponse.getData().getSuccess())
                    .reverseOrder(reverseOrderResponse.getData().getSuccess())
                    .build();

        }

        return PaackResponse.<OrderExchangeSuccessResponse, Error>builder()
                .data(response)
                .build();
    }

    /**
     * Create a new direct Order and a new reverse Order for the Store Model from the exchange order information.
     * It automatically takes care to set the pickup and delivery address and timeslot correctly.
     * It manages errors if one of the 2 orders fails.
     *
     * Args:
     * request: OrderExchangeRequest
     * Returns:
     * OrderExchangeSuccessResponse | Error
     * @param request
     * @return
     */
    public PaackResponse<OrderExchangeSuccessResponse, Error> exchangeWithStore(ExchangeOrder request) {

        Order directOrder = buildOrderForExchange(request, DeliveryType.DIRECT);
        Order reverseOrder = buildOrderForExchange(request, DeliveryType.REVERSE);
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        directOrder.setClusters(Collections.singletonList(uuidAsString));
        reverseOrder.setClusters(Collections.singletonList(uuidAsString));

        PaackResponse<OrderCreateSuccessResponse, Error> directOrderResponse = create(directOrder);
        PaackResponse<OrderCreateSuccessResponse, Error> reverseOrderResponse = create(reverseOrder);

        if (directOrderResponse.getError() != null) {
            return PaackResponse.<OrderExchangeSuccessResponse, Error>builder()
                    .error(directOrderResponse.getError())
                    .build();
        }

        if (reverseOrderResponse.getError() != null) {
            return PaackResponse.<OrderExchangeSuccessResponse, Error>builder()
                    .error(reverseOrderResponse.getError())
                    .build();
        }
        OrderExchangeSuccessResponse response = null;

        if (directOrderResponse.getData() != null && reverseOrderResponse.getData() != null) {
            response = OrderExchangeSuccessResponse.builder()
                    .directOrder(directOrderResponse.getData().getSuccess())
                    .reverseOrder(reverseOrderResponse.getData().getSuccess())
                    .build();

        }

        return PaackResponse.<OrderExchangeSuccessResponse, Error>builder()
                .data(response)
                .build();
    }

    /**
     * Cancels an Order from the system. Canceling an order will not delete it from Paack system.
     * It will change its status to canceled and stop being processed by Paack.
     * Canceling an order via the API is only possible while the order has not been route yet for delivery.
     * If you need to cancel an order that was already routed, please contact Paack customer support.
     *
     * You will not be able to create a new order with the same External ID as a canceled order.
     * If you have a reason to re-create an order with the same External ID please contact Paack.
     *
     * Args:
     * orderId: Order's external ID
     * @param orderId
     * @return
     */
    public PaackResponse<Boolean, Error> cancelRequest(String orderId) {
        if (orderId == null || orderId.length() == 0) {
            return errorMessage("OrderId cannot be empty", "orderAPI.cancelRequest.orderId", "001");
        }
        try {
            PaackResponse<Void, Error> response = apiClient.invokeAPI(PaackEndpoint.order,
                    "DELETE",
                    Collections.singletonList(param(PaackConstants.PARAM_EXTERNAL_ID, orderId)),
                    null,
                    null,
                    new TypeReference<Void>() {
                    });
            log.info(response.toString());
            if (response.getError() != null) {
                return PaackResponse.<Boolean, Error>builder()
                        .data(false)
                        .build();
            } else {
                return PaackResponse.<Boolean, Error>builder()
                        .data(true)
                        .build();
            }

        } catch (ApiException e) {
            log.error("Update order parcel with barcode: %s slot failed", e);
            return errorMessage("OrderApi.cancelRequest", e.getMessage());
        }
    }

    /**
     * GetById
     * Query an Order by the External ID. If the Order is found, it returns an Order object.
     * Otherwise it returns an Error response indicating why the order was not returned.
     *
     * Args:
     * orderId: Order's external ID
     * Returns:
     * OrderResponse | Error
     * @param request
     * @return
     */
    public PaackResponse<OrderResponse, Error> getById(GetOrderRequest request) {
        try {
            Optional<Error> error = getOrderRequestValidator.checkForErrors(request);
            if (error.isPresent()) {
                return errorMessage(error.get());
            }
            return getOrder(request.getExternalId(), request.getInclude(), request.getLabelFormat());
        } catch (ApiException e) {
            log.error("Get order failed", e);
            return errorMessage("OrderApi.getById", e.getMessage());
        }
    }

    protected PaackResponse<OrderCreateSuccessResponse, Error> create(Order order) {
        Optional<Error> error = orderValidator.checkForErrors(order);
        if (error.isPresent()) {
            return errorMessage(error.get());
        }

        try {
            OrderDTO orderDTO = OrderConverter.toDTO(order);
            PaackResponse<OrderCreateSuccessResponse, Error> response = apiClient.invokeAPI(PaackEndpoint.order,
                    "POST",
                    null,
                    null,
                    orderDTO,
                    new TypeReference<OrderCreateSuccessResponse>() {
                    });
            log.info(response.toString());
            return response;
        } catch (ApiException e) {
            log.error("Create order failed", e);
            return errorMessage("OrderApi.create", e.getMessage());
        }
    }

    public PaackResponse<Order, Error> delete(String externalId) {
        if (externalId == null || externalId.length() == 0) {
            return errorMessage("OrderId cannot be empty", "orderAPI.delete.orderId", "001");
        }
        try {
            PaackResponse<Order, Error> response = apiClient.invokeAPI(PaackEndpoint.order,
                    "DELETE",
                    Collections.singletonList(param(PaackConstants.PARAM_EXTERNAL_ID, externalId)),
                    null,
                    null,
                    new TypeReference<Order>() {
                    });
            log.info(response.toString());
            return PaackResponse.<Order, Error>builder()
                    .data(response.getData())
                    .build();
        } catch (ApiException e) {
            log.error("Update order parcel with barcode: %s slot failed", e);
            return errorMessage("OrderApi.delete", e.getMessage());
        }
    }

    private PaackResponse<UpdateOrderResponse, Error> update(String externalId, Order order) throws ApiException {
        Optional<Error> error = orderValidator.checkForErrors(order);
        if (error.isPresent()) {
            return errorMessage(error.get());
        }

        if (order == null) {
            return errorMessage("Tried to update an order but passed null as parameter.", "order", "001");
        }
        if (order.getExternalId() == null || order.getExternalId().length() == 0) {
            return errorMessage("External Id cannot be empty", "OrderAPI.update.externalId.", "001");
        }

        OrderDTO orderDTO = OrderConverter.toDTO(order);
        PaackResponse<UpdateOrderResponse, Error> response = apiClient.invokeAPI(PaackEndpoint.order,
                "PUT",
                Collections.singletonList(param(PaackConstants.PARAM_EXTERNAL_IDS, externalId)),
                null,
                orderDTO,
                new TypeReference<UpdateOrderResponse>() {
                });

        log.info(response.toString());
        return response;
    }

    private PaackResponse<OrderResponse, Error> getOrder(String externalId, String include, String labelFormat)
            throws ApiException {
        if (externalId == null || externalId.length() == 0) {
            return errorMessage("ExternalId cannot be empty", "orderAPI.getOrder.externalId", "001");
        }

        PaackResponse<OrderResponseSuccessDTO, Error> response = apiClient.invokeAPI(PaackEndpoint.order,
                "GET",
                Collections.singletonList(param(PaackConstants.PARAM_EXTERNAL_IDS, externalId)),
                Stream.of(
                        param(PaackConstants.PARAM_INCLUDE, include),
                        param(PaackConstants.PARAM_LABEL_FORMAT, labelFormat)
                ).collect(Collectors.toList()),
                null,
                new TypeReference<OrderResponseSuccessDTO>() {
                });
        log.info(response.toString());
        return PaackResponse.<OrderResponse, Error>builder()
                .data(response.getData() != null ? OrderResponseConverter.toModel(response.getData().getSuccess()) : null)
                .error(response.getError())
                .build();
    }

    private Order buildOrderForExchange(ExchangeOrder request, DeliveryType deliveryType) {
        return Order.builder()
                .customer(request.getCustomer())
                .deliveryAddress(request.getDeliveryAddress())
                .expectedDeliveryTs(request.getExpectedDeliveryTs())
                .parcels(request.getDirectParcels())
                .orderDetails(request.getOrderDetails())
                .pickupAddress(request.getPickUpAddress())
                .expectedPickUpTs(request.getExpectedPickUpTs())
                .externalId(deliveryType == DeliveryType.DIRECT ? request.getDirectExternalID() : request.getReverseExternalID())
                .deliveryType(deliveryType)
                .serviceType(request.getServiceType())
                .undeliverableAddress(request.getUndeliverableAddress())
                .expectedPickUpTs(request.getExpectedPickUpTs())
                .clusters(request.getClusters())
                .pickupAddress(request.getPickUpAddress())
                .insured(request.getInsured())
                .build();
    }
}
