package rest

import (
	"github.com/PaackEng/paack-go-sdk/types"
	"github.com/stretchr/testify/assert"
	"testing"
)

var retailerLocationApi *RetailerLocationApi

func RetailerLocationApiInit() *RetailerLocationApi {
	if retailerLocationApi != nil {
		return retailerLocationApi
	}

	apiClient, config, customLogger, _, retailerAudience := BaseInit()
	retailerLocationApi = NewRetailerLocationApi(retailerAudience, config.Result.DataConfig.Domain.Coverage.Staging, &config.Result.DataConfig.Resources, *apiClient, customLogger)

	return retailerLocationApi
}

var retailerLocationId = "008f3945-7c38-4569-8704-62702b8487d0"

var retailerLocation = types.RetailerLocation{
	Address: types.Address{
		City:     "Barcelona",
		Country:  "ES",
		County:   "Comtat de Barcelona",
		Line1:    "Via Augusta",
		Line2:    "17, Principal",
		PostCode: "08006",
	},
	Alias:        "TestingRL28123",
	DcID:         "dc149383-2071-4ea1-95de-57603ef47d66",
	RetailerID:   "ab4ce553-ad39-4cfa-ba81-0f4b9b22efcf",
	LocationName: "Testing Retailer28123",
	RetailerName: "Testing Retailer28",
	Type:         "Store",
}

func Test_CreateRetailerLocation(t *testing.T) {

	RetailerLocationApiInit()

	got, err := retailerLocationApi.CreateRetailerLocation(retailerLocation)

	assert.NotNil(t, got)
	assert.Nil(t, err)

	if err == nil {
		retailerLocationId = got.Success.RetailerLocationID
	}
}

func Test_GetRetailerLocation(t *testing.T) {

	RetailerLocationApiInit()

	request := GetRetailerLocationRequest{
		//ID: "71c375e2-eccf-48e5-813f-6d0b9d756f26",
		RetailerID: "ab4ce553-ad39-4cfa-ba81-0f4b9b22efcf",
		//RetailerName: "Testing Retailer28",
		//	LocationName: "Testing Retailer28",
		//	Country:      "ES",
		//	City:         "Barcelona",
		//	PostCode:     "08006",
		//	DcID:         "dc149383-2071-4ea1-95de-57603ef47d66",
		//	Alias:        "TestingRL28",
		//Type: "Store",
	}

	got, err := retailerLocationApi.GetRetailerLocation(request)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

var patchRetailerLocation = PatchRetailerLocationRequest{
	Address: &types.Address{
		City:     "Barcelona2",
		Country:  "ES",
		County:   "Comtat de Barcelona2",
		Line1:    "Via Augusta2",
		Line2:    "17, Principal2",
		PostCode: "08006",
	},
	Alias: "TestingRL282",
	DcID:  "dc149383-2071-4ea1-95de-57603ef47d66",
	Type:  "Store",
}

func Test_UpdateRetailerLocation(t *testing.T) {

	RetailerLocationApiInit()

	got, err := retailerLocationApi.UpdateRetailerLocation(retailerLocationId, patchRetailerLocation)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_DeleteRetailerLocation(t *testing.T) {

	RetailerLocationApiInit()

	_, err := retailerLocationApi.DeleteRetailerLocation(retailerLocationId)

	assert.Nil(t, err)
}
