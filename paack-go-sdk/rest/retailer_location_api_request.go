package rest

import "github.com/PaackEng/paack-go-sdk/types"

type GetRetailerLocationRequest struct {
	ID           string
	RetailerID   string
	RetailerName string
	LocationName string
	Country      string
	City         string
	PostCode     string
	DcID         string
	Alias        string
	Type         string
}

type PatchRetailerLocationRequest struct {
	Address *types.Address `json:"address,omitempty"`
	Alias   string         `json:"alias,omitempty"`
	DcID    string         `json:"dc_id,omitempty"`
	Type    string         `json:"type,omitempty"`
}
