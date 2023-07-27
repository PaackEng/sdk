package rest

import (
	"encoding/json"
	"fmt"
	"github.com/PaackEng/paack-go-sdk/api_client"
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/mixin"
	"github.com/PaackEng/paack-go-sdk/types/responses"
	"io"
	"strconv"
	"strings"
)

/*
TrackingPullApi - The Pull API is a public service available for retailers.
You can use it to retrieve the status of your orders, including all of their order history details.
Your request is queried against Paack's system.
The API returns the relevant information based on the request made.

More details can be found in the Paack API documentation: https://paack.readme.io/reference/orders-tracking-requests
*/
type TrackingPullApi struct {
	BaseApi
}

func NewTrackingPullApi(audience, domain string, path *mixin.Resources, client api_client.ApiClient, logger logger.Logger) *TrackingPullApi {

	return &TrackingPullApi{
		*NewBaseApi(client, domain, audience, path, logger),
	}
}

/*
OrderStatusGet - Retrieve the last status of the order with the specified ID.

Args:

	orderId (string): Id of the order

Returns:

	response (OrderStatusGetResponse | ErrorResponse): either the status of the order or error response
*/
func (t *TrackingPullApi) OrderStatusGet(orderId string) (successResponse *responses.OrderStatusResponse, errorResponse *responses.ErrorResponse) {
	t.logger.Info("Calling TrackingPullApi.OrderStatusGet method")

	// validate orderId
	if len(orderId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("orderId cannot be empty"), "001", "orderId", t.logger)
		return
	}

	url := fmt.Sprintf("%s%s", t.domain, t.path.TrackingPull.LastStatus)
	url = strings.Replace(url, "{externalIds}", orderId, 1)

	resp, err := t.client.Get(url, true, t.audience)
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", t.logger)
		return
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", body), strconv.Itoa(resp.StatusCode), "", t.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", t.logger)
		return
	}

	return
}

/*
OrderStatusList - Retrieves the order status list of the specified external id(s) for a timeframe in ISO UTC date format.
It also takes a count value and an after and before value for pagination.
If the response has more than one page it will automatically retrieve the following pages and concatenate the responses.
If one of the responses have an error it will return the error.

Args:

	orderIds ([]string]): A list of order Ids to fetch
	start (string): Start date in ISO format
	end (string): End date in ISO format
	count (int): Number of events returned in the response. The default and minimum number is 10. The maximum is 50
	before (string): The previous ID given to a page of events which can be called in the endpoint for pagination purposes
	after (string): The next ID given to a page of events which can be called in the endpoint for pagination purposes

Returns:

	response (OrderStatusResponse | ErrorResponse): either the status of the order or error response
*/
func (t *TrackingPullApi) OrderStatusList(request OrderStatusListRequest) (successResponse *responses.OrderStatusResponse, errorResponse *responses.ErrorResponse) {
	t.logger.Info("Calling TrackingPullApi.OrderStatusList method")

	err, source, code := request.Validate()
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while validating request: %s", err), code, source, t.logger)
		return
	}

	apiResponse := responses.OrderStatusResponse{}

	url := t.computeOrderStatusListUrl(request)
	errorResponse = t.sendRequest(url, &apiResponse)

	if errorResponse == nil {
		successResponse = &apiResponse
	}

	return
}

func (t *TrackingPullApi) sendRequest(url string, successResponse *responses.OrderStatusResponse) (errorResponse *responses.ErrorResponse) {
	resp, err := t.client.Get(url, true, t.audience)
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", t.logger)
		return
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", body), strconv.Itoa(resp.StatusCode), "", t.logger)
		return
	}

	var apiResponse *responses.OrderStatusListResponse
	err = json.Unmarshal(body, &apiResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", t.logger)
		return
	}

	for _, v := range apiResponse.Data {
		successResponse.Data = append(successResponse.Data, v.Attributes)
	}

	if len(apiResponse.Links.Next.Href) > 0 {
		if strings.HasPrefix(apiResponse.Links.Next.Href, "http") {
			url = apiResponse.Links.Next.Href
		} else if strings.HasPrefix(apiResponse.Links.Next.Href, "/api/v3/tracking") {
			url = fmt.Sprintf("%s%s", strings.ReplaceAll(t.domain, "/api/v3/tracking", ""), apiResponse.Links.Next.Href)
		} else {
			url = fmt.Sprintf("%s%s", t.domain, apiResponse.Links.Next.Href)
		}
	} else {
		return
	}

	return t.sendRequest(url, successResponse)
}

func (t *TrackingPullApi) EventTranslationGet(lang string) (successResponse *responses.EventTranslationGetResponse, errorResponse *responses.ErrorResponse) {
	t.logger.Info("Calling TrackingPullApi.EventTranslationGet method")

	// validate orderId
	if len(lang) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("language cannot be empty"), "", "", t.logger)
		return
	}

	url := fmt.Sprintf("%s%s", t.domain, t.path.TrackingPull.Translation)
	url = strings.Replace(url, "{lang}", lang, 1)

	resp, err := t.client.Get(url, true, t.audience)
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", t.logger)
		return
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", body), strconv.Itoa(resp.StatusCode), "", t.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", t.logger)
		return
	}

	return
}

func (t *TrackingPullApi) computeOrderStatusListUrl(request OrderStatusListRequest) string {
	url := fmt.Sprintf("%s%s", t.domain, t.path.TrackingPull.StatusList)
	params := make([]string, 0)

	if len(request.OrderIds) > 0 {
		params = append(params, fmt.Sprintf("externalIds=%s", strings.Join(request.OrderIds, ";")))
	}

	if len(request.StartDate) > 0 {
		params = append(params, fmt.Sprintf("start=%s", request.StartDate))
	}

	if len(request.EndDate) > 0 {
		params = append(params, fmt.Sprintf("end=%s", request.EndDate))
	}

	if len(request.Before) > 0 {
		params = append(params, fmt.Sprintf("before=%s", request.Before))
	}

	if len(request.After) > 0 {
		params = append(params, fmt.Sprintf("after=%s", request.After))
	}

	if request.Count > 0 {
		params = append(params, fmt.Sprintf("count=%d", request.Count))
	}

	url += strings.Join(params, "&")

	return url
}
