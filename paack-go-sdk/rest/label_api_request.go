package rest

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/types"
)

type LabelCreateRequest struct {
	TemplateID           interface{}      `json:"template_id"`
	ExternalID           string           `json:"external_id"`
	ServiceType          string           `json:"service_type"`
	DeliveryType         string           `json:"delivery_type"`
	DeliveryInstructions string           `json:"delivery_instructions"`
	ParcelNumber         int              `json:"parcel_number"`
	Parcels              []types.Parcel   `json:"parcels"`
	Customer             types.Customer   `json:"customer"`
	DeliveryAddress      types.Address    `json:"delivery_address"`
	PickUpAddress        types.Address    `json:"pick_up_address"`
	ExpectedDeliveryTs   types.ExpectedTs `json:"expected_delivery_ts"`
}

func (l *LabelCreateRequest) Validate() (err error, source, code string) {
	for _, v := range l.Parcels {
		err, source, code = v.Validate()
		if err != nil {
			return fmt.Errorf("Parcels[%s]: %s", v.Barcode, err), fmt.Sprintf("Parcels[%s].%s", v.Barcode, source), code
		}
	}

	err, source, code = l.DeliveryAddress.Validate()
	if err != nil {
		return fmt.Errorf("DeliveryAddress: %s", err), "DeliveryAddresss." + source, code
	}

	err, source, code = l.PickUpAddress.Validate()
	if err != nil {
		return fmt.Errorf("PickUpAddress: %s", err), "PickUpAddress." + source, code
	}

	err, source, code = l.Customer.Validate(true)
	if err != nil {
		return fmt.Errorf("Customer: %s", err), "Customer." + source, code
	}

	err, source, code = l.ExpectedDeliveryTs.Validate(l.DeliveryAddress)
	if err != nil {
		return fmt.Errorf("ExpectedDeliveryTs: %s", err), "ExpectedDeliveryTs." + source, code
	}

	return
}
