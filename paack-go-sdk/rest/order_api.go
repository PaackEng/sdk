package rest

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/PaackEng/paack-go-sdk/api_client"
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/mixin"
	"github.com/PaackEng/paack-go-sdk/resources"
	"github.com/PaackEng/paack-go-sdk/types"
	"github.com/PaackEng/paack-go-sdk/types/responses"
	"github.com/google/uuid"
	"io"
	"strconv"
)

/*
OrderApi - Allows you to easily manage orders.

The Order model / structure is required as a parameter for the following functions:
CreateWithWarehouse, CreateWithStore, UpdateOrder, UpsertOrderWithWarehouse, UpsertOrderWithStore. It follows a similar structure as expected by the API but has a few improvements to make it easier to populate with values.

Each parcel contains WeightUnits and LengthUnits. Possible values for WeightUnits are ["mg", "g", "kg"]. Possible values for LengthUnits are ["mm", "cm", "m"]. These units are used by the SDK to normalize Height, Length, Width to 'cm' and Weight to 'kg'.

All times specified in the input values will be converted to UTC by the SDK. If they are already in UTC, they will be left as it is. The SDK will use the country and postcode of the delivery address to infer the Timezone of the delivery time you supplied, when converting to UTC.

The phone number, if provided without a prefix, will be automatically prefixed by the SDK using the country of the delivery address. This is needed as Paack API requires the phone number to have a prefix.

More details can be found in the Paack API documentation: https://paack.readme.io/reference/orders
*/
type OrderApi struct {
	BaseApi
}

func NewOrderApi(audience, domain string, path *mixin.Resources, client api_client.ApiClient, logger logger.Logger) *OrderApi {

	return &OrderApi{
		*NewBaseApi(client, domain, audience, path, logger),
	}
}

/*
CreateWithWarehouse - Create a new Order for the Warehouse Model. Enable model by default when called. Applies validations specific for the Warehouse Model.

Args:

	payload: OrderSchema payload
	labelFormat: LabelFormat

Returns:

	OrderCreateSuccessResponse | ErrorResponse
*/
func (o *OrderApi) CreateWithWarehouse(request types.Order, labelFormat *string) (successResponse *responses.OrderCreateSuccessResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.CreateWithWarehouse method")

	payload, err, source, code := MapOrderCreateRequest(request)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while mapping order to CreateRequest payload: %s", err), code, source, o.logger)
		return
	}

	payload.WithModel = int(resources.WarehouseModel)

	return o.sendCreateRequest(payload, labelFormat)
}

/*
CreateWithStore - Create a new Order for the Store Model. Enable model by default when called. Applies validations specific for the Store Model.

Args:

	payload: OrderSchema payload
	labelFormat: LabelFormat

Returns:

	OrderCreateSuccessResponse | ErrorResponse
*/
func (o *OrderApi) CreateWithStore(request types.Order, labelFormat *string) (successResponse *responses.OrderCreateSuccessResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.CreateWithStore method")

	request.IsStoreModel = true
	payload, err, source, code := MapOrderCreateRequest(request)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while mapping order to CreateRequest payload: %s", err), code, source, o.logger)
		return
	}

	payload.WithModel = int(resources.StoreModel)

	return o.sendCreateRequest(payload, labelFormat)
}

/*
GetById - Cancels an Order from the system. Canceling an order will not delete it from Paack system. It will change its status to canceled and stop being processed by Paack.
Canceling an order via the API is only possible while the order has not been route yet for delivery. If you need to cancel an order that was already routed, please contact Paack customer support.

You will not be able to create a new order with the same External ID as a canceled order. If you have a reason to re-create an order with the same External ID please contact Paack.

Args:

	orderId: Order's Id

Returns:

	GetOrderByIdResponse | ErrorResponse
*/
func (o *OrderApi) GetById(orderId string, labelFormat *string) (successResponse *responses.GetOrderByIdResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.GetById method")

	// validate orderId
	if len(orderId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("orderId cannot be empty"), "001", "orderId", o.logger)
		return
	}

	url := fmt.Sprintf("%s%s/%s", o.domain, o.path.Order, orderId)

	if labelFormat != nil {
		if *labelFormat != "zpl" && *labelFormat != "pdf" {
			errorResponse = responses.NewError(fmt.Sprintf("Invalid labelFormat('pdf', 'zpl'): %s", *labelFormat), "002", "LabelFormat", o.logger)
			return
		}

		url = fmt.Sprintf("%s?include=label&labelFormat=%s", url, *labelFormat)
	}

	resp, err := o.client.Get(url, true, o.audience)
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", resp.Status), strconv.Itoa(resp.StatusCode), "", o.logger)
		return
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", o.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", o.logger)
		return
	}

	return
}

/*
UpdateParcel - Update a single parcel from an Order payload.
The function makes a request to take the existing order values and then update the parcels with the new parcel details.
The parcel is matched based on the barcode.

Args:

	parcel: ParcelSchema payload
	orderId: Order's Id

Returns:

	UpdateOrderResponse | ErrorResponse
*/
func (o *OrderApi) UpdateParcel(orderId string, parcel types.Parcel) (successResponse *responses.UpdateOrderResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.UpdateParcel method")

	// validate orderId
	if len(orderId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("orderId cannot be empty"), "001", "orderId", o.logger)
		return
	}

	// validate request
	err, source, code := parcel.Validate()
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while validating parcel: %s", err), code, source, o.logger)
		return
	}

	// get order details
	getOrderByIdResponse, errorResponse := o.GetById(orderId, nil)
	if errorResponse != nil {
		return
	}

	orderDetails := responses.GetOrderByIdResponse{}

	// read order details
	jsonString, _ := json.Marshal(getOrderByIdResponse)
	err = json.Unmarshal(jsonString, &orderDetails)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("error while reading getOrderById response"), "", "", o.logger)
		return
	}

	// update parcel
	for k, v := range orderDetails.Success.Parcels {
		if v.Barcode == parcel.Barcode {
			orderDetails.Success.Parcels[k] = parcel
		}
	}

	// create payload
	payloadBytes := new(bytes.Buffer)
	err = json.NewEncoder(payloadBytes).Encode(orderDetails.Success)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while encoding payload: %s", err), "", "", o.logger)
		return
	}

	return o.sendUpdateRequest(orderId, orderDetails.Success)
}

/*
UpdateParcels - Bulk update parcels from an Order payload.
The function makes a request to take the existing order values and then update the parcels with the new parcels details.
Parcels where the barcode is matching will have their values updated.
Parcels there were in the order but are not in the new list will be removed.
Parcels that were not in the order before but now are added will be added to the order.

Args:

	parcelList: ParcelSchema payload
	orderId: Order's Id

Returns:

	UpdateOrderResponse | ErrorResponse
*/
func (o *OrderApi) UpdateParcels(orderId string, parcelList []types.Parcel) (successResponse *responses.UpdateOrderResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.UpdateParcel method")

	// validate orderId
	if len(orderId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("orderId cannot be empty"), "001", "orderId", o.logger)
		return
	}

	// validate request
	for _, v := range parcelList {
		err, source, code := v.Validate()
		if err != nil {
			errorResponse = responses.NewError(fmt.Sprintf("Error while validating parcel: %s", err), fmt.Sprintf("parcelList[%s].%s", v.Barcode, source), code, o.logger)

			return
		}
	}

	// get order details
	getOrderByIdResponse, errorResponse := o.GetById(orderId, nil)
	if errorResponse != nil {
		return
	}

	orderDetails := responses.GetOrderByIdResponse{}

	// read order details
	jsonString, _ := json.Marshal(getOrderByIdResponse)
	err := json.Unmarshal(jsonString, &orderDetails)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("error while reading getOrderById response"), "", "", o.logger)
		return
	}

	sendUpdateOrderRequest := false

	// update parcel
	for k, v := range parcelList {
		for kp, p := range orderDetails.Success.Parcels {
			if v.Barcode == p.Barcode {
				// check if the user sent only the barcode and populate fields from api
				if v.Length == 0 && v.Weight == 0 && v.Height == 0 && v.Width == 0 {
					parcelList[k] = orderDetails.Success.Parcels[kp]
				}
				sendUpdateOrderRequest = true
				break
			}
		}
	}

	// update parcels sent by users and delete all others
	orderDetails.Success.Parcels = parcelList

	if !sendUpdateOrderRequest {
		return
	}

	// create payload
	payloadBytes := new(bytes.Buffer)
	err = json.NewEncoder(payloadBytes).Encode(orderDetails.Success)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while encoding payload: %s", err), "", "", o.logger)
		return
	}

	// send update request
	return o.sendUpdateRequest(orderId, orderDetails.Success)
}

/*
UpdateDeliveryAddress - Update delivery address from an Order payload.
The function makes a request to take the existing values and then updates them with the new values from the request

Args:

	address: delivery address
	orderId: Order's Id

Returns:

	UpdateOrderResponse | ErrorResponse
*/
func (o *OrderApi) UpdateDeliveryAddress(orderId string, address types.Address) (successResponse *responses.UpdateOrderResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.UpdateDeliveryAddress method")

	// validate orderId
	if len(orderId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("orderId cannot be empty"), "001", "orderId", o.logger)
		return
	}

	// validate request
	err, source, code := address.Validate()
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while validating address: %s", err), code, "Address."+source, o.logger)
		return
	}

	// get order details
	getOrderByIdResponse, errorResponse := o.GetById(orderId, nil)
	if errorResponse != nil {
		return
	}

	orderDetails := responses.GetOrderByIdResponse{}

	// read order details
	jsonString, _ := json.Marshal(getOrderByIdResponse)
	err = json.Unmarshal(jsonString, &orderDetails)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("error while reading getOrderById response"), "", "", o.logger)
		return
	}

	// update address
	orderDetails.Success.DeliveryAddress = address

	// send update request
	return o.sendUpdateRequest(orderId, orderDetails.Success)
}

/*
UpdateCustomerContactDetails - Update delivery address from an Order payload.
The function makes a request to take the existing values and then updates them with the new values from the request

Args:

	contactInfo: contact info
	orderId: Order's Id

Returns:

	UpdateOrderResponse | ErrorResponse
*/
func (o *OrderApi) UpdateCustomerContactDetails(orderId string, contactInfo types.ContactInfo) (successResponse *responses.UpdateOrderResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.UpdateCustomerContactDetails method")

	// validate orderId
	if len(orderId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("orderId cannot be empty"), "001", "orderId", o.logger)
		return
	}

	// validate request
	err, source, code := contactInfo.Validate("")
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while validating contactInfo: %s", err), code, "ContactInfo."+source, o.logger)
		return
	}

	// get order details
	getOrderByIdResponse, errorResponse := o.GetById(orderId, nil)
	if errorResponse != nil {
		return
	}

	orderDetails := responses.GetOrderByIdResponse{}

	// read order details
	jsonString, _ := json.Marshal(getOrderByIdResponse)
	err = json.Unmarshal(jsonString, &orderDetails)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("error while reading getOrderById response"), "", "", o.logger)
		return
	}

	// update contact info
	orderDetails.Success.Customer.ContactInfo = contactInfo

	// create payload
	payloadBytes := new(bytes.Buffer)
	err = json.NewEncoder(payloadBytes).Encode(orderDetails.Success)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while encoding payload: %s", err), "", "", o.logger)
		return
	}

	// send the update request
	url := fmt.Sprintf("%s%s/%s", o.domain, o.path.Order, orderId)
	resp, err := o.client.Put(url, payloadBytes.Bytes(), true, o.audience)
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", resp.Status), strconv.Itoa(resp.StatusCode), "", o.logger)
		return
	}

	// read the response
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", o.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", o.logger)
		return
	}

	return
}

/*
UpdateOrder - Update all changeable parameters from an Order payload.
The function makes a request to take the existing values and then updates them with the new values from the request

Args:

	order: order info

Returns:

	UpdateOrderResponse | ErrorResponse
*/
func (o *OrderApi) UpdateOrder(order types.Order) (successResponse *responses.UpdateOrderResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.UpdateOrder method")

	// validate request
	err, _, _ := order.Validate()
	if err != nil {
		return nil, nil
	}

	payload, err, source, code := types.MapOrderDTO(order)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while mapping order to GerOrderDTO payload: %s", err), code, source, o.logger)
		return
	}

	// send update request
	return o.sendUpdateRequest(order.ExternalID, payload)
}

/*
UpsertOrderWithWarehouse - Update all changeable parameters from an Order payload with Warehouse Model if order exists. Otherwise, create new order
The function makes a request to take the existing values and then updates them with the new values from the request

Args:

	order: order info

Returns:

	OrderCreateSuccessResponse | ErrorResponse
*/
func (o *OrderApi) UpsertOrderWithWarehouse(order types.Order) (successResponse *responses.OrderCreateSuccessResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.UpsertOrderWithWarehouse method")

	// validate request
	payload, err, source, code := MapOrderCreateRequest(order)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while mapping order to CreateRequest payload: %s", err), code, source, o.logger)
		return
	}

	// get order details
	getOrderByIdResponse, errorResponse := o.GetById(order.ExternalID, nil)
	if errorResponse != nil {
		if len(errorResponse.Error) > 0 && errorResponse.Error[0].Code == "404" {
			o.logger.Info("New order will be created")
			payload.WithModel = int(resources.WarehouseModel)
			createResponse, failed := o.sendCreateRequest(payload, nil)

			successResponse = createResponse
			errorResponse = failed

			return
		}

		return
	}

	orderDetails := responses.GetOrderByIdResponse{}

	// read order details
	jsonString, _ := json.Marshal(getOrderByIdResponse)
	err = json.Unmarshal(jsonString, &orderDetails)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("error while reading getOrderById response"), "", "", o.logger)
		return
	}

	if orderDetails.Success.ExternalID != order.ExternalID {
		errorResponse = responses.NewError(fmt.Sprintf("unable to update order"), "", "", o.logger)
		return
	}

	updateResponse, failed := o.UpdateOrder(order)
	if updateResponse != nil {
		successResponse = &responses.OrderCreateSuccessResponse{
			Success: responses.CreateOrderSuccess{
				TrackingID: updateResponse.Success.ExternalID,
			},
		}
	}
	errorResponse = failed

	return
}

/*
UpsertOrderWithStore - Update all changeable parameters from an Order payload with Store Model if order exists. Otherwise, create new order

Args:

	order: order info

Returns:

	OrderCreateSuccessResponse | ErrorResponse
*/
func (o *OrderApi) UpsertOrderWithStore(order types.Order) (successResponse *responses.OrderCreateSuccessResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.UpsertOrderWithStore method")

	order.IsStoreModel = true

	// validate request
	payload, err, source, code := MapOrderCreateRequest(order)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while mapping order to CreateRequest payload: %s", err), code, source, o.logger)
		return
	}

	// get order details
	getOrderByIdResponse, errorResponse := o.GetById(order.ExternalID, nil)
	if errorResponse != nil {
		if len(errorResponse.Error) > 0 && errorResponse.Error[0].Code == "404" {
			o.logger.Info("New order will be created")
			payload.WithModel = int(resources.StoreModel)
			createResponse, failed := o.sendCreateRequest(payload, nil)

			successResponse = createResponse
			errorResponse = failed

			return
		}

		return
	}

	orderDetails := responses.GetOrderByIdResponse{}

	// read order details
	jsonString, _ := json.Marshal(getOrderByIdResponse)
	err = json.Unmarshal(jsonString, &orderDetails)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("error while reading getOrderById response"), "", "", o.logger)
		return
	}

	if orderDetails.Success.ExternalID != order.ExternalID {
		errorResponse = responses.NewError(fmt.Sprintf("unable to update order"), "", "", o.logger)
		return
	}

	updateResponse, failed := o.UpdateOrder(order)
	if updateResponse != nil {
		successResponse = &responses.OrderCreateSuccessResponse{
			Success: responses.CreateOrderSuccess{
				TrackingID: updateResponse.Success.ExternalID,
			},
		}
	}
	errorResponse = failed

	return
}

/*
ExchangeWithWarehouse - Create a new direct Order and a new reverse Order with Warehouse Model

Args:

	request: OrderExchangeRequest

Returns:

	OrderExchangeSuccessResponse | ErrorResponse
*/
func (o *OrderApi) ExchangeWithWarehouse(request OrderExchangeRequest) (successResponse *responses.OrderExchangeSuccessResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.ExchangeWithWarehouse method")

	directOrder, reverseOrder, err, source, code := MapOrderExchangeRequest(request)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while mapping order to ExchangeRequest payload: %s", err), code, source, o.logger)
		return
	}

	// generate cluster uuid
	id := uuid.New()

	directOrder.Clusters = []string{id.String()}
	directOrder.WithModel = int(resources.WarehouseModel)

	reverseOrder.Clusters = []string{id.String()}
	reverseOrder.WithModel = int(resources.WarehouseModel)

	// create direct order
	successDirectResponse, errorResponse := o.sendCreateRequest(directOrder, nil)
	if errorResponse != nil {
		return
	}

	// prepare response
	successResponse = &responses.OrderExchangeSuccessResponse{
		DirectOrder: successDirectResponse.Success,
	}

	// create reverse order
	successReverseResponse, errorResponse := o.sendCreateRequest(reverseOrder, nil)
	if errorResponse != nil {
		return
	}

	// prepare response
	successResponse.ReverseOrder = successReverseResponse.Success

	return
}

/*
ExchangeWithStore - Create a new direct Order and a new reverse Order with Store Model

Args:

	request: OrderExchangeRequest

Returns:

	OrderExchangeSuccessResponse | ErrorResponse
*/
func (o *OrderApi) ExchangeWithStore(request OrderExchangeRequest) (successResponse *responses.OrderExchangeSuccessResponse, errorResponse *responses.ErrorResponse) {

	directOrder, reverseOrder, err, source, code := MapOrderExchangeRequest(request)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while mapping order to ExchangeRequest payload: %s", err), code, source, o.logger)
		return
	}

	// generate cluster uuid
	id := uuid.New()

	directOrder.Clusters = []string{id.String()}
	directOrder.WithModel = int(resources.StoreModel)

	reverseOrder.Clusters = []string{id.String()}
	reverseOrder.WithModel = int(resources.StoreModel)

	// create direct order
	successDirectResponse, errorResponse := o.sendCreateRequest(directOrder, nil)
	if errorResponse != nil {
		return
	}

	// prepare response
	successResponse = &responses.OrderExchangeSuccessResponse{
		DirectOrder: successDirectResponse.Success,
	}

	// create reverse order
	successReverseResponse, errorResponse := o.sendCreateRequest(reverseOrder, nil)
	if errorResponse != nil {
		return
	}

	// prepare response
	successResponse.ReverseOrder = successReverseResponse.Success

	return
}

/*
CancelRequest - Cancel an Order from the system

Args:

	orderId: Order's Id

Returns:

	DeleteOrderResponse | ErrorResponse
*/
func (o *OrderApi) CancelRequest(orderId string) (successResponse *responses.DeleteOrderResponse, errorResponse *responses.ErrorResponse) {
	o.logger.Info("Calling OrderApi.CancelRequest method")

	// validate orderId
	if len(orderId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("orderId cannot be empty"), "001", "orderId", o.logger)
		return
	}

	// send the delete request
	url := fmt.Sprintf("%s%s/%s", o.domain, o.path.Order, orderId)
	resp, err := o.client.Delete(url, true, o.audience)
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", resp.Status), strconv.Itoa(resp.StatusCode), "", o.logger)
		return
	}

	// read the response
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", o.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", o.logger)
		return
	}

	return
}

func (o *OrderApi) sendCreateRequest(payload OrderCreateRequest, labelFormat *string) (successResponse *responses.OrderCreateSuccessResponse, errorResponse *responses.ErrorResponse) {
	payloadBytes := new(bytes.Buffer)
	err := json.NewEncoder(payloadBytes).Encode(payload)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while encoding payload: %s", err), "", "", o.logger)
		return
	}

	url := fmt.Sprintf("%s%s", o.domain, o.path.Order)
	if labelFormat != nil {
		if *labelFormat != "zpl" && *labelFormat != "pdf" {
			errorResponse = responses.NewError(fmt.Sprintf("Invalid labelFormat('pdf', 'zpl'): %s", *labelFormat), "002", "LabelFormat", o.logger)
			return
		}

		url = fmt.Sprintf("%s?include=label&labelFormat=%s", url, *labelFormat)
	}

	resp, err := o.client.Post(url, payloadBytes.Bytes(), true, o.audience)

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", o.logger)
		return
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", body), strconv.Itoa(resp.StatusCode), "", o.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", o.logger)
		return
	}

	return
}

func (o *OrderApi) sendUpdateRequest(orderId string, order types.OrderDTO) (successResponse *responses.UpdateOrderResponse, errorResponse *responses.ErrorResponse) {
	// create payload
	payloadBytes := new(bytes.Buffer)
	err := json.NewEncoder(payloadBytes).Encode(order)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while encoding payload: %s", err), "", "", o.logger)
		return
	}

	// send the update request
	url := fmt.Sprintf("%s%s/%s", o.domain, o.path.Order, orderId)
	resp, err := o.client.Put(url, payloadBytes.Bytes(), true, o.audience)
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", resp.Status), strconv.Itoa(resp.StatusCode), "", o.logger)
		return
	}

	// read the response
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", o.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", o.logger)
		return
	}

	return
}
