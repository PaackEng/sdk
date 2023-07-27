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
PodApi - Proof of Delivery API allows you to easily validate that your orders have been delivered.
It works by extracting additional delivery verification data that is sent
from the Paack driver application to Paack's database.

More details can be found in the Paack API documentation: https://paack.readme.io/reference/orders-proof-of-delivery
*/
type PodApi struct {
	BaseApi
}

func NewPodApi(audience, domain string, path *mixin.Resources, client api_client.ApiClient, logger logger.Logger) *PodApi {

	return &PodApi{
		*NewBaseApi(client, domain, audience, path, logger),
	}
}

/*
DeliveryVerifications - Retrieves the delivery verifications for the retailer of the ID specified in the path parameter.
*/
func (t *PodApi) DeliveryVerifications(orderId string) (successResponse *responses.DeliveryVerificationsResponse, errorResponse *responses.ErrorResponse) {
	t.logger.Info("Calling PodApi.DeliveryVerifications method")

	// validate orderId
	if len(orderId) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("orderId cannot be empty"), "001", "orderId", t.logger)
		return
	}

	url := fmt.Sprintf("%s%s", t.domain, t.path.Pod)
	url = strings.Replace(url, "{external_id}", orderId, 1)

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
