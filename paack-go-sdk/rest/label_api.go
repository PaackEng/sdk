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
	"io"
	"strconv"
)

/*
LabelApi - Create and return labels for an order.

More details can be found in the Paack API documentation: https://paack.readme.io/reference/labels
*/
type LabelApi struct {
	BaseApi
}

func NewLabelApi(audience, domain string, path *mixin.Resources, client api_client.ApiClient, logger logger.Logger) *LabelApi {

	return &LabelApi{
		*NewBaseApi(client, domain, audience, path, logger),
	}
}

/*
LabelCreate - Create and return a label for an order.
Defines the format of the label: 1 is for ZPL, nil or missing property returns a PDF

Args:

	payload (LabelCreateRequest)
	labelFormat (LabelFormat)
*/
func (t *LabelApi) LabelCreate(payload LabelCreateRequest, labelFormat resources.LabelFormat) (successResponse responses.LabelCreateResponse, errorResponse *responses.ErrorResponse) {
	t.logger.Info("Calling LabelApi.LabelCreate method")

	if len(payload.DeliveryAddress.City) > 58 {
		errorResponse = responses.NewError("Error while validating payload: delivery_address city can take up to 58 characters", "002", "DeliveryAddress.City", t.logger)
		return
	}

	if len(payload.DeliveryAddress.Line1)+len(payload.DeliveryAddress.Line2) > 60 {
		errorResponse = responses.NewError("Error while validating payload: a max. of approx. 60 characters (2 graphic lines) can be displayed for delivery_address line1 and line2 combined", "002", "DeliveryAddress.Line1", t.logger)
		return
	}

	if len(payload.DeliveryAddress.Line2) > 30 {
		errorResponse = responses.NewError("Error while validating payload: a max. of approx. 30 characters can be displayed for delivery_address line2", "002", "DeliveryAddress.Line2", t.logger)
		return
	}

	if len(payload.PickUpAddress.City) > 43 {
		errorResponse = responses.NewError("Error while validating payload: pick_up_address city can take up to 43 characters", "002", "PickUpAddress.City", t.logger)
		return
	}

	if len(payload.PickUpAddress.Line1)+len(payload.PickUpAddress.Line2) > 43 {
		errorResponse = responses.NewError("Error while validating payload: a max. of approx. 43 characters (2 graphic lines) can be displayed for pick_up_address line1 and line2 combined", "002", "PickUpAddress.Line1", t.logger)
		return
	}

	if len(payload.PickUpAddress.Line2) > 22 {
		errorResponse = responses.NewError("Error while validating payload: a max. of approx. 22 characters can be displayed for pick_up_address line2", "002", "PickUpAddress.Line2", t.logger)
		return
	}

	err, source, code := payload.Validate()
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while validating payload: %s", err), code, source, t.logger)
		return
	}

	// check the format of the label
	if labelFormat == resources.SingleZplLabel {
		payload.TemplateID = 1
	} else if labelFormat == resources.MultiZplLabel {
		payload.TemplateID = 2
	} else {
		payload.TemplateID = nil
	}

	payloadBytes := new(bytes.Buffer)
	err = json.NewEncoder(payloadBytes).Encode(payload)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while encoding payload: %s", err), "", "", t.logger)
		return
	}

	url := fmt.Sprintf("%s%s", t.domain, t.path.Label)
	resp, err := t.client.Post(url, payloadBytes.Bytes(), true, t.audience)
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", t.logger)
		return
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", body), strconv.Itoa(resp.StatusCode), "", t.logger)
		return
	}

	if resp.StatusCode == 206 {
		errorMessage := responses.ErrorMessageResponse{}
		errUnmarshal := json.Unmarshal(body, &errorMessage)
		if errUnmarshal == nil {
			errorResponse = responses.NewError(errorMessage.Error.Message, errorMessage.Error.Code, "", t.logger)
			return
		}
	}

	// check the format of the label
	if labelFormat == resources.SingleZplLabel || labelFormat == resources.MultiZplLabel {
		successResponse = responses.LabelCreateZPLResponse{
			IsZpl: true,
			Label: string(body[:]),
		}
	} else {
		successResponse = responses.LabelCreatePDFResponse{
			IsZpl: false,
			Label: bytes.NewReader(body),
		}
	}

	return
}

/*
LabelCreateByParcel - Create and return a label FOR EACH parcel in the order.
Defines the format of the label: 1 is for ZPL, nil or missing property returns a PDF

Args:

	payload (LabelCreateRequest)
	labelFormat (LabelFormat)
*/
func (t *LabelApi) LabelCreateByParcel(payload LabelCreateRequest, labelFormat resources.LabelFormat) (successResponse []responses.LabelCreateResponse, errorResponse *responses.ErrorResponse) {
	t.logger.Info("Calling LabelApi.LabelCreateByParcel method")

	err, source, code := payload.Validate()
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while validating payload: %s", err), code, source, t.logger)
		return
	}

	if len(payload.Parcels) == 0 {
		errorResponse = responses.NewError(fmt.Sprintf("No parcel provided to create label."), "001", "Parcels", t.logger)
		return
	} else if len(payload.Parcels) == 1 {
		parcelResponse, errResponse := t.LabelCreate(payload, labelFormat)
		if errResponse != nil {
			errorResponse = errResponse
			return
		}

		successResponse = append(successResponse, parcelResponse)

		return
	}

	parcelList := payload.Parcels
	for _, v := range parcelList {
		payload.Parcels = make([]types.Parcel, 0)
		payload.Parcels = append(payload.Parcels, v)

		parcelResponse, errResponse := t.LabelCreate(payload, labelFormat)
		if errResponse != nil {
			errorResponse = errResponse
			return
		}

		successResponse = append(successResponse, parcelResponse)
	}

	return
}
