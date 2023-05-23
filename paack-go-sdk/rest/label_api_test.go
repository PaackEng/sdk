package rest

import (
	"github.com/PaackEng/paack-go-sdk/resources"
	"github.com/PaackEng/paack-go-sdk/types"
	"github.com/stretchr/testify/assert"
	"testing"
)

var labelApi *LabelApi

func InitLabelApi() *LabelApi {

	if labelApi != nil {
		return labelApi
	}

	apiClient, config, customLogger, orderAudience, _ := BaseInit()
	labelApi = NewLabelApi(orderAudience, config.Result.DataConfig.Domain.Label.Staging, &config.Result.DataConfig.Resources, *apiClient, customLogger)

	return labelApi
}

var labelForOneParcelOrder = LabelCreateRequest{
	Customer: types.Customer{
		FirstName: "Martín",
		LastName:  "Fierro",
	},
	DeliveryAddress: types.Address{
		City:     "Barcelona",
		Country:  "IT",
		Line1:    "Via Augusta",
		Line2:    "17, Principal",
		PostCode: "08006",
	},
	PickUpAddress: types.Address{
		City:     "Barcelona",
		Country:  "IT",
		Line1:    "Via Augusta",
		Line2:    "17, Principal",
		PostCode: "08006",
	},
	ExpectedDeliveryTs: types.ExpectedTs{
		Start: types.DateTs{
			Date: "2022-02-28",
			Time: "10:00:00",
		},
		End: types.DateTs{
			Date: "2022-02-28",
			Time: "12:00:00",
		},
	},
	ExternalID: "5cef0f5-1a0-44-b0f-96faa0",
	Parcels: []types.Parcel{
		{
			Barcode:    "034724878233029420",
			Height:     28.2,
			Length:     38.1,
			Width:      48.3,
			Weight:     12.1,
			WeightUnit: "kg",
			LengthUnit: "cm",
		},
	},
	ServiceType: "NT4",
}
var labelForMultiParcelOrder = LabelCreateRequest{
	TemplateID:   1,
	ParcelNumber: 2,
	Customer: types.Customer{
		FirstName: "Martín",
		LastName:  "Fierro",
	},
	DeliveryAddress: types.Address{
		City:     "Barcelona",
		Country:  "ES",
		Line1:    "Via Augusta",
		Line2:    "17, Principal",
		PostCode: "08006",
	},
	PickUpAddress: types.Address{
		City:     "Barcelona",
		Country:  "ES",
		Line1:    "Via Augusta",
		Line2:    "17, Principal",
		PostCode: "08006",
	},
	ExpectedDeliveryTs: types.ExpectedTs{
		Start: types.DateTs{
			Date: "2022-02-28",
			Time: "10:00:00",
		},
		End: types.DateTs{
			Date: "2022-05-25",
			Time: "12:00:00",
		},
	},
	ExternalID: "5cef0f5-1a0-44-b0f-96faa0",
	Parcels: []types.Parcel{
		{
			Barcode:    "034724878233029420",
			Height:     28.2,
			Length:     38.1,
			Width:      48.3,
			Weight:     12.1,
			Type:       "standard",
			WeightUnit: "kg",
			LengthUnit: "cm",
		},
		{
			Barcode:    "034724878233029499",
			Height:     19.0,
			Length:     12.33,
			Width:      33.1,
			Weight:     18.3,
			Type:       "standard",
			WeightUnit: "kg",
			LengthUnit: "cm",
		},
		{
			Barcode:    "034724878233029437",
			Height:     18.5,
			Length:     9.3,
			Width:      13.33,
			Weight:     2.5,
			Type:       "standard",
			WeightUnit: "kg",
			LengthUnit: "cm",
		},
	},
	ServiceType: "NT4",
}

func Test_LabelCreatePdf(t *testing.T) {

	InitLabelApi()
	got, err := labelApi.LabelCreate(labelForOneParcelOrder, resources.PdfLabel)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_LabelCreateSingleZplLabel(t *testing.T) {

	InitLabelApi()
	got, err := labelApi.LabelCreate(labelForOneParcelOrder, resources.SingleZplLabel)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_LabelCreateMultiZplLabel(t *testing.T) {

	InitLabelApi()
	got, err := labelApi.LabelCreate(labelForOneParcelOrder, resources.MultiZplLabel)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_LabelCreateByParcel(t *testing.T) {

	InitLabelApi()
	got, err := labelApi.LabelCreateByParcel(labelForMultiParcelOrder, resources.SingleZplLabel)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}
