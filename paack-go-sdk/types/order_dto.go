package types

import "fmt"

type OrderDTO struct {
	Status                    string         `json:"status"`
	CodCurrency               string         `json:"cod_currency"`
	CodValue                  float64        `json:"cod_value"`
	Customer                  Customer       `json:"customer"`
	DeliveryType              string         `json:"delivery_type"`
	DeliveryAddress           Address        `json:"delivery_address"`
	DeliveryInstructions      string         `json:"delivery_instructions"`
	ExpectedDeliveryTs        ExpectedTs     `json:"expected_delivery_ts"`
	ExpectedPickUpTs          ExpectedTs     `json:"expected_pick_up_ts"`
	ExternalID                string         `json:"external_id"`
	Parcels                   []Parcel       `json:"parcels"`
	OrderDetails              []ExtraDetails `json:"order_details"`
	PickUpAddress             Address        `json:"pick_up_address"`
	PickUpInstructions        string         `json:"pick_up_instructions"`
	ServiceType               string         `json:"service_type"`
	UndeliverableAddress      Address        `json:"undeliverable_address"`
	UndeliverableInstructions string         `json:"undeliverable_instructions"`
	DeliveryModel             string         `json:"delivery_model"`
	InsuredValue              float64        `json:"insured_value"`
	InsuredCurrency           string         `json:"insured_currency"`
	Labels                    string         `json:"labels"`
}

func MapOrderDTO(order Order) (result OrderDTO, err error, source, code string) {

	err, source, code = order.Validate()
	if err != nil {
		return result, fmt.Errorf("%s", err), "order." + source, code
	}

	return OrderDTO{
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
