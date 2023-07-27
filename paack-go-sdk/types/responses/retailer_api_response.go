package responses

import "github.com/PaackEng/paack-go-sdk/types"

type GetRetailerLocationResponse struct {
	RetailerLocations []types.RetailerLocation `json:"retailer_locations"`
}

type RetailerLocationResponse struct {
	Success struct {
		RetailerLocationID string `json:"retailer_location_id"`
	} `json:"success"`
}
