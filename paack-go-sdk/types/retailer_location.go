package types

import "fmt"

type RetailerLocation struct {
	Address      Address `json:"address"`
	Alias        string  `json:"alias"`
	DcID         string  `json:"dc_id"`
	RetailerID   string  `json:"retailer_id,omitempty"`
	ID           string  `json:"id,omitempty"`
	LocationName string  `json:"location_name"`
	RetailerName string  `json:"retailer_name"`
	Type         string  `json:"type"`
}

func (r *RetailerLocation) Validate() (err error, source, code string) {

	err, source, code = r.Address.Validate()
	if err != nil {
		return fmt.Errorf("%s", err), "Address." + source, code
	}

	if len(r.RetailerName) == 0 {
		return fmt.Errorf("invalid retailer name"), "RetailerName", "001"
	}

	if len(r.LocationName) == 0 {
		return fmt.Errorf("invalid location name"), "LocationName", "001"
	}

	return
}
