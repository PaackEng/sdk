package types

import "fmt"

type Order struct {
	Customer                  Customer       `json:"customer"`
	DeliveryAddress           Address        `json:"delivery_address"`
	ExpectedDeliveryTs        ExpectedTs     `json:"expected_delivery_ts"`
	Parcels                   []Parcel       `json:"parcels"`
	OrderDetails              []ExtraDetails `json:"order_details"`
	PickUpAddress             Address        `json:"pick_up_address"`
	ExpectedPickUpTs          ExpectedTs     `json:"expected_pick_up_ts"`
	ExternalID                string         `json:"external_id"`
	DeliveryType              string         `json:"delivery_type"`
	ServiceType               string         `json:"service_type"`
	Clusters                  []string       `json:"clusters"`
	UndeliverableInstructions string         `json:"undeliverable_instructions"`
	Insured                   Money          `json:"insured"`
	CashOnDelivery            Money          `json:"cash_on_delivery"`
	UndeliverableAddress      Address        `json:"undeliverable_address"`
	IsStoreModel              bool
}

func (o *Order) setIsUtcFields(value bool) {
	o.ExpectedDeliveryTs.Start.IsUtc = value
	o.ExpectedDeliveryTs.End.IsUtc = value
	o.ExpectedPickUpTs.Start.IsUtc = value
	o.ExpectedPickUpTs.End.IsUtc = value
}

func (o *Order) Validate() (err error, source, code string) {

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

	for _, v := range o.Parcels {
		err, source, code = v.Validate()
		if err != nil {
			return fmt.Errorf("%s", err), fmt.Sprintf("Parcels[%s].%s", v.Barcode, source), code
		}
	}

	err, source, code = o.DeliveryAddress.Validate()
	if err != nil {
		return fmt.Errorf("%s", err), "DeliveryAddress." + source, code
	}

	err, source, code = o.PickUpAddress.Validate()
	if err != nil {
		return fmt.Errorf("%s", err), "PickUpAddress." + source, code
	}

	err, source, code = o.Customer.Validate(false)
	if err != nil {
		return fmt.Errorf("%s", err), "Customer." + source, code
	}

	if o.IsStoreModel {
		err, source, code = o.ExpectedPickUpTs.Validate(o.PickUpAddress)
		if err != nil {
			return fmt.Errorf("%s", err), "ExpectedPickUpTs." + source, code
		}
	}

	err, source, code = o.ExpectedDeliveryTs.Validate(o.DeliveryAddress)
	if err != nil {
		return fmt.Errorf("%s", err), "ExpectedDeliveryTs." + source, code
	}

	return

}
