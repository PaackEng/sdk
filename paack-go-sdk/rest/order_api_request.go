package rest

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/types"
)

type OrderCreateRequest struct {
	ExternalID                string               `json:"external_id"`
	Customer                  types.Customer       `json:"customer"`
	DeliveryAddress           types.Address        `json:"delivery_address"`
	ExpectedDeliveryTs        types.ExpectedTs     `json:"expected_delivery_ts"`
	DeliveryInstructions      string               `json:"delivery_instructions"`
	DeliveryType              string               `json:"delivery_type"`
	ServiceType               string               `json:"service_type"`
	Parcels                   []types.Parcel       `json:"parcels"`
	OrderDetails              []types.ExtraDetails `json:"order_details"`
	PickUpAddress             types.Address        `json:"pick_up_address"`
	ExpectedPickUpTs          types.ExpectedTs     `json:"expected_pick_up_ts"`
	PickUpInstructions        string               `json:"pick_up_instructions"`
	CodValue                  float64              `json:"cod_value"`
	CodCurrency               string               `json:"cod_currency"`
	UndeliverableAddress      types.Address        `json:"undeliverable_address"`
	UndeliverableInstructions string               `json:"undeliverable_instructions"`
	InsuredValue              float64              `json:"insured_value"`
	InsuredCurrency           string               `json:"insured_currency"`
	WithModel                 int                  `json:"withModel"`
	Clusters                  []string             `json:"clusters"`
}

/*
MapOrderCreateRequest - This is a custom function to convert some internal fields
and remove those that are not need when creating an order.
*/
func MapOrderCreateRequest(order types.Order) (result OrderCreateRequest, err error, source, code string) {

	err, source, code = order.Validate()
	if err != nil {
		return result, fmt.Errorf("%s", err), "order." + source, code
	}

	return OrderCreateRequest{
		ExternalID:                order.ExternalID,
		Customer:                  order.Customer,
		DeliveryAddress:           order.DeliveryAddress,
		ExpectedDeliveryTs:        order.ExpectedDeliveryTs,
		DeliveryInstructions:      order.DeliveryAddress.Instructions,
		DeliveryType:              order.DeliveryType,
		ServiceType:               order.ServiceType,
		Parcels:                   order.Parcels,
		OrderDetails:              order.OrderDetails,
		PickUpAddress:             order.PickUpAddress,
		ExpectedPickUpTs:          order.ExpectedPickUpTs,
		PickUpInstructions:        order.PickUpAddress.Instructions,
		CodValue:                  order.CashOnDelivery.Amount,
		CodCurrency:               order.CashOnDelivery.Currency,
		UndeliverableAddress:      order.UndeliverableAddress,
		UndeliverableInstructions: order.UndeliverableAddress.Instructions,
		InsuredValue:              order.Insured.Amount,
		InsuredCurrency:           order.Insured.Currency,
	}, nil, "", ""
}

type OrderExchangeRequest struct {
	DirectExternalID     string
	ReverseExternalID    string
	Customer             types.Customer
	DeliveryAddress      types.Address
	ExpectedDeliveryTs   types.ExpectedTs
	DeliveryType         string
	ServiceType          string
	DirectParcels        []types.Parcel
	ReverseParcels       []types.Parcel
	OrderDetails         []types.ExtraDetails
	PickUpAddress        types.Address
	ExpectedPickUpTs     types.ExpectedTs
	Insured              types.Money
	CashOnDelivery       types.Money
	UndeliverableAddress types.Address
	Clusters             []string
}

func MapOrderExchangeRequest(request OrderExchangeRequest) (directOrder, reverseOrder OrderCreateRequest, err error, source, code string) {

	err, source, code = request.Validate()
	if err != nil {
		return directOrder, reverseOrder, fmt.Errorf("%s", err), source, code
	}

	directOrder = OrderCreateRequest{
		Customer:                  request.Customer,
		DeliveryAddress:           request.DeliveryAddress,
		ExpectedDeliveryTs:        request.ExpectedDeliveryTs,
		Parcels:                   request.DirectParcels,
		OrderDetails:              request.OrderDetails,
		PickUpAddress:             request.PickUpAddress,
		ExpectedPickUpTs:          request.ExpectedPickUpTs,
		ExternalID:                request.DirectExternalID,
		DeliveryInstructions:      request.DeliveryAddress.Instructions,
		DeliveryType:              "direct",
		ServiceType:               request.ServiceType,
		Clusters:                  request.Clusters,
		PickUpInstructions:        request.PickUpAddress.Instructions,
		UndeliverableInstructions: request.UndeliverableAddress.Instructions,
		UndeliverableAddress:      request.UndeliverableAddress,
		InsuredValue:              request.Insured.Amount,
		InsuredCurrency:           request.Insured.Currency,
	}

	reverseOrder = OrderCreateRequest{
		Customer:                  request.Customer,
		DeliveryAddress:           request.DeliveryAddress,
		ExpectedDeliveryTs:        request.ExpectedDeliveryTs,
		Parcels:                   request.ReverseParcels,
		OrderDetails:              request.OrderDetails,
		PickUpAddress:             request.DeliveryAddress,
		ExpectedPickUpTs:          request.ExpectedDeliveryTs,
		ExternalID:                request.ReverseExternalID,
		DeliveryInstructions:      request.DeliveryAddress.Instructions,
		DeliveryType:              "reverse",
		ServiceType:               request.ServiceType,
		Clusters:                  request.Clusters,
		PickUpInstructions:        request.PickUpAddress.Instructions,
		UndeliverableInstructions: request.UndeliverableAddress.Instructions,
		UndeliverableAddress:      request.UndeliverableAddress,
		InsuredValue:              request.Insured.Amount,
		InsuredCurrency:           request.Insured.Currency,
	}

	return
}

func (o *OrderExchangeRequest) Validate() (err error, source, code string) {

	if len(o.DirectExternalID) == 0 {
		return fmt.Errorf("invalid DirectExternalID. PLease insert the externalId of the direct order"), "DirectExternalID", "001"
	}

	if len(o.ReverseExternalID) == 0 {
		return fmt.Errorf("invalid ReverseExternalID. PLease insert the externalId of the reverse order"), "ReverseExternalID", "001"
	}

	if len(o.Customer.Address.City) == 0 || len(o.Customer.Address.Country) == 0 ||
		len(o.Customer.Address.County) == 0 || len(o.Customer.Address.PostCode) == 0 ||
		len(o.Customer.Address.Line1) == 0 || len(o.Customer.Address.Line2) == 0 {
		o.Customer.Address.City = o.DeliveryAddress.City
		o.Customer.Address.Country = o.DeliveryAddress.Country
		o.Customer.Address.County = o.DeliveryAddress.County
		o.Customer.Address.PostCode = o.DeliveryAddress.PostCode
		o.Customer.Address.Line1 = o.DeliveryAddress.Line1
		o.Customer.Address.Line2 = o.DeliveryAddress.Line2
	}

	for _, v := range o.DirectParcels {
		err, source, code = v.Validate()
		if err != nil {
			return fmt.Errorf("DirectParcels[%s]: %s", v.Barcode, err), fmt.Sprintf("DirectParcels[%s].%s", v.Barcode, source), code
		}
	}

	for _, v := range o.ReverseParcels {
		err, source, code = v.Validate()
		if err != nil {
			return fmt.Errorf("ReverseParcels[%s]: %s", v.Barcode, err), fmt.Sprintf("ReverseParcels[%s].%s", v.Barcode, source), code
		}
	}

	err, source, code = o.DeliveryAddress.Validate()
	if err != nil {
		return fmt.Errorf("DeliveryAddress: %s", err), "DeliveryAddresss." + source, code
	}

	err, source, code = o.PickUpAddress.Validate()
	if err != nil {
		return fmt.Errorf("PickUpAddress: %s", err), "PickUpAddress." + source, code
	}

	err, source, code = o.Customer.Validate(false)
	if err != nil {
		return fmt.Errorf("Customer: %s", err), "Customer." + source, code
	}

	err, source, code = o.ExpectedPickUpTs.Validate(o.PickUpAddress)
	if err != nil {
		return fmt.Errorf("ExpectedPickUpTs: %s", err), "ExpectedPickUpTs." + source, code
	}

	err, source, code = o.ExpectedDeliveryTs.Validate(o.DeliveryAddress)
	if err != nil {
		return fmt.Errorf("ExpectedDeliveryTs: %s", err), "ExpectedDeliveryTs." + source, code
	}

	return

}
