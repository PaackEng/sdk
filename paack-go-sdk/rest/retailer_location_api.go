package rest

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/PaackEng/paack-go-sdk/api_client"
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/mixin"
	"github.com/PaackEng/paack-go-sdk/types"
	"github.com/PaackEng/paack-go-sdk/types/responses"
	"io"
	"strconv"
)

/*
RetailerLocationApi - is a public service available for retailers.
You can use it to manage the retailers' locations.

More details can be found in the Paack API documentation: https://paack.readme.io/reference/orders-tracking-requests
*/
type RetailerLocationApi struct {
	BaseApi
}

func NewRetailerLocationApi(audience, domain string, path *mixin.Resources, client api_client.ApiClient, logger logger.Logger) *RetailerLocationApi {

	return &RetailerLocationApi{
		*NewBaseApi(client, domain, audience, path, logger),
	}
}

/*
GetRetailerLocation - Retrieve a list of retailer locations

Args:

	GetRetailerLocationRequest

Returns:

	GetRetailerLocationResponse | ErrorResponse
*/
func (r *RetailerLocationApi) GetRetailerLocation(request GetRetailerLocationRequest) (successResponse *responses.GetRetailerLocationResponse, errorResponse *responses.ErrorResponse) {
	r.logger.Info("Calling RetailerLocationApi.GetRetailerLocation method")

	if len(request.RetailerID) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("retailerId cannot be empty"), "001", "RetailerID", r.logger)
		return
	}

	url := r.computeGetRetailerLocationUrl(request)

	resp, err := r.client.Get(url, true, r.audience)
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", resp.Status), strconv.Itoa(resp.StatusCode), "", r.logger)
		return
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", r.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", r.logger)
		return
	}

	return
}

/*
CreateRetailerLocation - Creates a new retailer location with a name, alias, type (store / warehouse) and address.

Args:

	payload: The retailer location data to create

Returns:

	RetailerLocationResponse | ErrorResponse
*/
func (r *RetailerLocationApi) CreateRetailerLocation(payload types.RetailerLocation) (successResponse *responses.RetailerLocationResponse, errorResponse *responses.ErrorResponse) {
	r.logger.Info("Calling RetailerLocationApi.CreateRetailerLocation method")

	err, source, code := payload.Validate()
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while validating payload: %s", err), code, source, r.logger)
		return
	}

	payloadBytes := new(bytes.Buffer)
	err = json.NewEncoder(payloadBytes).Encode(payload)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while encoding payload: %s", err), "", "", r.logger)
		return
	}

	url := fmt.Sprintf("%s%s", r.domain, "/rls")
	resp, err := r.client.Post(url, payloadBytes.Bytes(), true, r.audience)

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", r.logger)
		return
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", body), strconv.Itoa(resp.StatusCode), "", r.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", r.logger)
		return
	}

	return
}

/*
UpdateRetailerLocation - Update an existing retailer location

Args:

	retailerLocationId: The retailer location ID to update
	payload: The retailer location data to update

Returns:

	RetailerLocationResponse | ErrorResponse
*/
func (r *RetailerLocationApi) UpdateRetailerLocation(retailerLocationId string, payload PatchRetailerLocationRequest) (successResponse *responses.RetailerLocationResponse, errorResponse *responses.ErrorResponse) {
	r.logger.Info("Calling RetailerLocationApi.UpdateRetailerLocation method")

	// validate retailerId
	if len(retailerLocationId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("retailerLocationId cannot be empty"), "", "", r.logger)
		return
	}

	// validate request
	if payload.Address != nil {
		err, source, code := payload.Address.Validate()
		if err != nil {
			errorResponse = responses.NewError(fmt.Sprintf("Error while validating address: %s", err), code, "Address."+source, r.logger)
			return
		}
	}

	// create payload
	payloadBytes := new(bytes.Buffer)
	err := json.NewEncoder(payloadBytes).Encode(payload)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while encoding payload: %s", err), "", "", r.logger)
		return
	}

	// send the update request
	url := fmt.Sprintf("%s%s/%s", r.domain, "/rls", retailerLocationId)
	resp, err := r.client.Patch(url, payloadBytes.Bytes(), true, r.audience)
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", resp.Status), strconv.Itoa(resp.StatusCode), "", r.logger)
		return
	}

	// read the response
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", r.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", r.logger)
		return
	}

	return
}

/*
DeleteRetailerLocation - Remove a retailer location

Args:

	retailerLocationId: The retailer location ID to update

Returns:

	DeleteOrderResponse | ErrorResponse
*/
func (r *RetailerLocationApi) DeleteRetailerLocation(retailerLocationId string) (successResponse *responses.DeleteOrderResponse, errorResponse *responses.ErrorResponse) {
	r.logger.Info("Calling RetailerLocationApi.DeleteRetailerLocation method")

	// validate retailerLocationId
	if len(retailerLocationId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("retailerLocationId cannot be empty"), "", "", r.logger)
		return
	}

	// send the delete request
	url := fmt.Sprintf("%s%s/%s", r.domain, "/rls", retailerLocationId)
	resp, err := r.client.Delete(url, true, r.audience)
	// read the response
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", r.logger)
		return
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", body), strconv.Itoa(resp.StatusCode), "", r.logger)
		return
	}

	if resp.StatusCode == 204 || resp.StatusCode >= 300 {
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", r.logger)
		return
	}

	return
}

func (r *RetailerLocationApi) computeGetRetailerLocationUrl(request GetRetailerLocationRequest) string {
	url := fmt.Sprintf("%s%s%s", r.domain, "/rls?retailer_id=", request.RetailerID)

	if len(request.ID) > 0 {
		url += fmt.Sprintf("&id=%s", request.ID)
	}

	if len(request.RetailerName) > 0 {
		url += fmt.Sprintf("&retailer_name=%s", request.RetailerName)
	}

	if len(request.LocationName) > 0 {
		url += fmt.Sprintf("&location_name=%s", request.LocationName)
	}

	if len(request.Country) > 0 {
		url += fmt.Sprintf("&country=%s", request.Country)
	}

	if len(request.City) > 0 {
		url += fmt.Sprintf("&city=%s", request.City)
	}

	if len(request.PostCode) > 0 {
		url += fmt.Sprintf("&post_code=%s", request.PostCode)
	}

	if len(request.DcID) > 0 {
		url += fmt.Sprintf("&dc_id=%s", request.DcID)
	}

	if len(request.Alias) > 0 {
		url += fmt.Sprintf("&alias=%s", request.Alias)
	}

	if len(request.Type) > 0 {
		url += fmt.Sprintf("&type=%s", request.Type)
	}

	return url
}
