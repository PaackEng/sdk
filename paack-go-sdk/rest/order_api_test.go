package rest

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/types"
	"github.com/stretchr/testify/assert"
	"math/rand"
	"testing"
	"time"
)

var orderApi *OrderApi

func InitOrderApi() *OrderApi {

	if orderApi != nil {
		return orderApi
	}

	apiClient, config, customLogger, orderAudience, _ := BaseInit()
	orderApi = NewOrderApi(orderAudience, config.Result.DataConfig.Domain.Order.Staging, &config.Result.DataConfig.Resources, *apiClient, customLogger)

	order.ExternalID = createOrderId()

	return orderApi
}

func createOrderId() string {
	rand.Seed(time.Now().UnixNano())

	return "200000V3009" + fmt.Sprintf("%d", rand.Intn(1000))
}

func Test_CreateWithWarehouse(t *testing.T) {

	InitOrderApi()
	got, err := orderApi.CreateWithWarehouse(order, nil)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_CreateZplLabel(t *testing.T) {

	InitOrderApi()
	labelFormat := "zpl"
	got, err := orderApi.CreateWithWarehouse(order, &labelFormat)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_CreateWithStore(t *testing.T) {

	InitOrderApi()
	order.ExternalID = createOrderId()

	got, err := orderApi.CreateWithStore(order, nil)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_GetById(t *testing.T) {

	InitOrderApi()
	got, err := orderApi.GetById(order.ExternalID, nil)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_GetByIdPdfLabel(t *testing.T) {

	InitOrderApi()
	labelFormat := "pdf"
	got, err := orderApi.GetById(order.ExternalID, &labelFormat)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_UpdateParcel(t *testing.T) {

	InitOrderApi()
	parcel := types.Parcel{
		Barcode:    "DRINK6TEST-1-2",
		Height:     100.0,
		Length:     100.0,
		Type:       "standard",
		Weight:     100.0,
		Width:      100.0,
		WeightUnit: "g",
		LengthUnit: "mm",
	}

	got, err := orderApi.UpdateParcel(order.ExternalID, parcel)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_UpdateParcels(t *testing.T) {

	InitOrderApi()
	got, err := orderApi.UpdateParcels(order.ExternalID, order.Parcels)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_UpdateDeliveryAddress(t *testing.T) {

	InitOrderApi()
	deliveryAddress := types.Address{
		City:     "Barcelona",
		Country:  "ES",
		County:   "Comtat de Barcelona",
		Line1:    "Via Augusta",
		Line2:    "17, Principal",
		PostCode: "08006",
	}
	got, err := orderApi.UpdateDeliveryAddress(order.ExternalID, deliveryAddress)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_UpdateCustomerContactDetails(t *testing.T) {

	InitOrderApi()
	contactInfo := types.ContactInfo{
		Email:          "test@paack.com",
		Phone:          "6664443331",
		HasGdprConsent: true,
	}
	got, err := orderApi.UpdateCustomerContactDetails(order.ExternalID, contactInfo)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_UpdateAllChangeableParameters(t *testing.T) {

	InitOrderApi()
	got, err := orderApi.UpdateOrder(upsertOrder)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_UpsertWithWarehouse(t *testing.T) {

	InitOrderApi()
	upsertOrder.ExternalID = "200000V300948"
	got, err := orderApi.UpsertOrderWithWarehouse(upsertOrder)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_ExchangeWithWarehouse(t *testing.T) {

	InitOrderApi()
	exchangeOrder.DirectExternalID = createOrderId()
	exchangeOrder.ReverseExternalID = createOrderId()

	got, err := orderApi.ExchangeWithWarehouse(exchangeOrder)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_ExchangeWithStore(t *testing.T) {

	InitOrderApi()
	exchangeOrder.DirectExternalID = createOrderId()
	exchangeOrder.ReverseExternalID = createOrderId()

	got, err := orderApi.ExchangeWithStore(exchangeOrder)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_CancelRequest(t *testing.T) {

	InitOrderApi()
	got, err := orderApi.CancelRequest(order.ExternalID)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

var order = types.Order{
	ExternalID: "200000V30091",
	Customer: types.Customer{
		FirstName: "Martín",
		LastName:  "Fierro",
		ContactInfo: types.ContactInfo{
			Email:          "martin.fierro@paack.com_1",
			Phone:          "+34397341804",
			HasGdprConsent: true,
		},
		Address: types.Address{
			City:     "Barcelona",
			Country:  "ES",
			County:   "Comtat de Barcelona",
			Line1:    "Via Augusta",
			Line2:    "17, Principal",
			PostCode: "08006",
		},
		Language: "fr",
	},
	DeliveryAddress: types.Address{
		City:         "Barcelona",
		Country:      "ES",
		County:       "Comtat de Barcelona",
		Line1:        "Via Augusta",
		Line2:        "18, Principal",
		PostCode:     "08006",
		Instructions: "Leave at the door",
	},
	ExpectedDeliveryTs: types.ExpectedTs{
		End: types.DateTs{
			Date: "2023-09-20",
			Time: "20:00:00",
		},
		Start: types.DateTs{
			Date: "2023-09-20",
			Time: "18:00:00",
		},
	},
	DeliveryType: "direct",
	ServiceType:  "ST2",
	Parcels: []types.Parcel{
		{
			Barcode:          "034724878233029421",
			Height:           28.2,
			Length:           38.1,
			Width:            48.3,
			Weight:           12.1,
			VolumetricWeight: 14.4,
			Type:             "standard",
			WeightUnit:       "kg",
			LengthUnit:       "cm",
		},
		{
			Barcode:          "DRINK6TEST-1-2",
			Height:           2.0,
			Length:           2.0,
			Type:             "standard",
			VolumetricWeight: 13.4,
			Weight:           2.0,
			Width:            2.0,
			WeightUnit:       "kg",
			LengthUnit:       "cm",
		},
	},
	PickUpAddress: types.Address{
		City:         "Barcelona",
		Country:      "ES",
		County:       "Comtat de Barcelona",
		Line1:        "Avinguda Diagonal",
		Line2:        "1234",
		PostCode:     "08021",
		Instructions: "Check opening hours",
	},
	ExpectedPickUpTs: types.ExpectedTs{
		End: types.DateTs{
			Date: "2023-09-20",
			Time: "10:00:00",
		},
		Start: types.DateTs{
			Date: "2023-09-20",
			Time: "08:00:00",
		},
	},
	Insured: types.Money{
		Amount:   20.20,
		Currency: "EUR",
	},
	CashOnDelivery: types.Money{
		Amount:   25.0,
		Currency: "EUR",
	},
	UndeliverableAddress: types.Address{
		City:         "Barcelona",
		Country:      "ES",
		County:       "Comtat de Barcelona",
		Line1:        "Avinguda Diagonal",
		Line2:        "1234",
		PostCode:     "08021",
		Instructions: "Open parcel",
	},
}
var upsertOrder = types.Order{
	ExternalID: "200000V300948",
	Customer: types.Customer{
		FirstName: "Martín1",
		LastName:  "Fierro1",
		ContactInfo: types.ContactInfo{
			Email:          "martin.fierro@paack.com1",
			Phone:          "+343973418041",
			HasGdprConsent: true,
		},
		Address: types.Address{
			City:     "Barcelona1",
			Country:  "ES",
			County:   "Comtat de Barcelona1",
			Line1:    "Via Augusta1",
			Line2:    "17, Principal1",
			PostCode: "08006",
		},
		Language: "en",
	},
	DeliveryAddress: types.Address{
		City:         "Barcelona1",
		Country:      "ES",
		County:       "Comtat de Barcelona1",
		Line1:        "Via Augusta1",
		Line2:        "18, Principal1",
		PostCode:     "08006",
		Instructions: "Leave at the door1",
	},
	ExpectedDeliveryTs: types.ExpectedTs{
		End: types.DateTs{
			Date: "2023-09-21",
			Time: "18:00:01",
		},
		Start: types.DateTs{
			Date: "2023-09-21",
			Time: "16:00:01",
		},
	},
	DeliveryType: "direct",
	ServiceType:  "ST2",
	Parcels: []types.Parcel{
		{
			Barcode:          "034724878233029421",
			Height:           28.2,
			Length:           38.2,
			Width:            48.2,
			Weight:           12.2,
			VolumetricWeight: 14.2,
			Type:             "standard",
			WeightUnit:       "kg",
			LengthUnit:       "cm",
		},
		{
			Barcode:          "DRINK6TEST-1-2",
			Height:           2.2,
			Length:           2.2,
			Type:             "standard",
			VolumetricWeight: 13.2,
			Weight:           2.2,
			Width:            2.2,
			WeightUnit:       "kg",
			LengthUnit:       "cm",
		},
	},
	PickUpAddress: types.Address{
		City:         "Barcelona",
		Country:      "ES",
		County:       "Comtat de Barcelona1",
		Line1:        "Avinguda Diagonal1",
		Line2:        "12341",
		PostCode:     "08021",
		Instructions: "Check opening hours1",
	},
	ExpectedPickUpTs: types.ExpectedTs{
		End: types.DateTs{
			Date: "2023-09-21",
			Time: "10:00:01",
		},
		Start: types.DateTs{
			Date: "2023-09-21",
			Time: "08:00:01",
		},
	},
	Insured: types.Money{
		Amount:   20.1,
		Currency: "EUR",
	},
	CashOnDelivery: types.Money{
		Amount:   25.1,
		Currency: "EUR",
	},
	UndeliverableAddress: types.Address{
		City:         "Barcelona",
		Country:      "ES",
		County:       "Comtat de Barcelona1",
		Line1:        "Avinguda Diagonal1",
		Line2:        "12341",
		PostCode:     "08021",
		Instructions: "Open parcel1",
	},
}
var exchangeOrder = OrderExchangeRequest{
	DirectExternalID:  "200000V300948",
	ReverseExternalID: "200000V300948",
	Customer: types.Customer{
		FirstName: "Martín1",
		LastName:  "Fierro1",
		ContactInfo: types.ContactInfo{
			Email:          "martin.fierro@paack.com1",
			Phone:          "+343973418041",
			HasGdprConsent: true,
		},
		Address: types.Address{
			City:     "Barcelona1",
			Country:  "ES",
			County:   "Comtat de Barcelona1",
			Line1:    "Via Augusta1",
			Line2:    "17, Principal1",
			PostCode: "08006",
		},
		Language: "en",
	},
	DeliveryAddress: types.Address{
		City:         "Barcelona1",
		Country:      "ES",
		County:       "Comtat de Barcelona1",
		Line1:        "Via Augusta1",
		Line2:        "18, Principal1",
		PostCode:     "08006",
		Instructions: "Leave at the door1",
	},
	ExpectedDeliveryTs: types.ExpectedTs{
		End: types.DateTs{
			Date: "2023-09-21",
			Time: "18:00:01",
		},
		Start: types.DateTs{
			Date: "2023-09-21",
			Time: "16:00:01",
		},
	},
	DeliveryType: "direct",
	ServiceType:  "ST2",
	DirectParcels: []types.Parcel{
		{
			Barcode:          "034724878233029421",
			Height:           28.2,
			Length:           38.2,
			Width:            48.2,
			Weight:           12.2,
			VolumetricWeight: 14.2,
			Type:             "standard",
			WeightUnit:       "kg",
			LengthUnit:       "cm",
		},
		{
			Barcode:          "DRINK6TEST-1-2",
			Height:           2.2,
			Length:           2.2,
			Type:             "standard",
			VolumetricWeight: 13.2,
			Weight:           2.2,
			Width:            2.2,
			WeightUnit:       "kg",
			LengthUnit:       "cm",
		},
	},
	PickUpAddress: types.Address{
		City:         "Barcelona",
		Country:      "ES",
		County:       "Comtat de Barcelona1",
		Line1:        "Avinguda Diagonal1",
		Line2:        "12341",
		PostCode:     "08021",
		Instructions: "Check opening hours1",
	},
	ExpectedPickUpTs: types.ExpectedTs{
		End: types.DateTs{
			Date: "2023-09-21",
			Time: "10:00:01",
		},
		Start: types.DateTs{
			Date: "2023-09-21",
			Time: "08:00:01",
		},
	},
	UndeliverableAddress: types.Address{
		City:         "Barcelona",
		Country:      "ES",
		County:       "Comtat de Barcelona1",
		Line1:        "Avinguda Diagonal1",
		Line2:        "12341",
		PostCode:     "08021",
		Instructions: "Open parcel1",
	},
}
