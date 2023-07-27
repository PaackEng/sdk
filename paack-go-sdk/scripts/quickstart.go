package scripts

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk"
	"github.com/PaackEng/paack-go-sdk/resources"
	"github.com/PaackEng/paack-go-sdk/rest"
	"github.com/PaackEng/paack-go-sdk/types"
	log "github.com/sirupsen/logrus"
	"os"
)

/*
	Quickstart script just to get started with the SDK.
	This quickstart shows you how to authenticate with the Paack API
*/

func main() {
	clientId := os.Getenv("CLIENT_ID")
	clientSecret := os.Getenv("CLIENT_SECRET")

	if clientSecret == "" || clientId == "" {
		fmt.Println("No credentials found")
		return
	}

	/*
		Any logger can be added here.
		The only requirement is that the logger implements the functions of the logger.Logger interface
		type Logger interface {
			Debug(args ...interface{})
			Info(args ...interface{})
			Error(args ...interface{})
		}
	*/
	customLogger := log.WithFields(log.Fields{
		"service": "paack-sdk",
	})

	// Initialize Paack client
	paackClient, err := paack_go_sdk.NewPaack(clientId, clientSecret, resources.StagingDomain, customLogger)
	if err != nil {
		return
	}

	// Create a new Order with Warehouse Model
	labelFormat := "pdf"
	_, err = paackClient.Order.CreateWithStore(order, &labelFormat)
	if err != nil {
		return
	}

	// Query an Order by ID
	_, err = paackClient.Order.GetById(order.ExternalID, nil)
	if err != nil {
		return
	}

	// Create and return a label for an order
	_, err = paackClient.Label.LabelCreate(labelCreateRequest, resources.PdfLabel)
	if err != nil {
		return
	}

	// Retrieve the last status of the order with the specified ID
	_, err = paackClient.TrackingPull.OrderStatusGet(order.ExternalID)
	if err != nil {
		return
	}
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
			Time: "19:00:00",
		},
		Start: types.DateTs{
			Date: "2023-09-20",
			Time: "16:00:00",
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
		},
		{
			Barcode:          "DRINK6TEST-1-2",
			Height:           2.0,
			Length:           2.0,
			Type:             "standard",
			VolumetricWeight: 13.4,
			Weight:           2.0,
			Width:            2.0,
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
var labelCreateRequest = rest.LabelCreateRequest{
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
	ExpectedDeliveryTs: types.ExpectedTs{
		Start: types.DateTs{
			Date: "2022-02-28",
			Time: "14:00:00",
		},
		End: types.DateTs{
			Date: "2022-02-28",
			Time: "16:00:00",
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
