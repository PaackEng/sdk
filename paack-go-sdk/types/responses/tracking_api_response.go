package responses

type OrderStatusResponse struct {
	Data  []OrderAttributes `json:"data"`
	Error interface{}       `json:"error"`
}

type OrderStatusListResponse struct {
	Data  []DataAttributes `json:"data"`
	Error []interface{}    `json:"error"`
	Links Links            `json:"links"`
}

type Links struct {
	Next struct {
		Href string `json:"href"`
	} `json:"next"`
}

type DataAttributes struct {
	Attributes OrderAttributes `json:"attributes"`
}

type EventTranslationGetResponse struct {
	Data []struct {
		EventID          string `json:"eventId"`
		EventName        string `json:"name"`
		EventDescription string `json:"description"`
	} `json:"data"`
	Error interface{} `json:"error"`
}

type OrderAttributes struct {
	ID               string `json:"id"`
	ExternalID       string `json:"external_id"`
	EventID          string `json:"event_id"`
	EventName        string `json:"event_name"`
	EventDescription string `json:"event_description"`
	IsOpen           bool   `json:"is_open"`
	Parcels          []struct {
		Barcode string  `json:"barcode"`
		Height  float64 `json:"height"`
		Length  float64 `json:"length"`
		Weight  float64 `json:"weight"`
		Width   float64 `json:"width"`
	} `json:"parcels"`
	DeliveryAttempt int    `json:"delivery_attempt"`
	DeliveryType    string `json:"delivery_type"`
	UpdatedAt       string `json:"updated_at"`
	ServiceType     string `json:"service_type"`
	Customer        struct {
		FirstName string `json:"first_name"`
		Phone     string `json:"phone"`
		Email     string `json:"email"`
	} `json:"customer"`
	DeliveryAddress struct {
		City     string `json:"city"`
		Country  string `json:"country"`
		Line1    string `json:"line1"`
		PostCode string `json:"post_code"`
	} `json:"delivery_address"`
	ExpectedDeliveryTs struct {
		Start struct {
			Date string `json:"date"`
			Time string `json:"time"`
		} `json:"start"`
		End struct {
			Date string `json:"date"`
			Time string `json:"time"`
		} `json:"end"`
	} `json:"expected_delivery_ts"`
	ExpectedPickupTs struct {
		Start struct {
			Date string `json:"date"`
			Time string `json:"time"`
		} `json:"start"`
		End struct {
			Date string `json:"date"`
			Time string `json:"time"`
		} `json:"end"`
	} `json:"expected_pickup_ts"`
	PickUpAddress struct {
		City     string `json:"city"`
		Country  string `json:"country"`
		Line1    string `json:"line1"`
		PostCode string `json:"post_code"`
	} `json:"pick_up_address"`
	OrderDetails []struct {
		Name  string `json:"name"`
		Type  string `json:"type"`
		Value string `json:"value"`
	} `json:"order_details"`
	DeliveryInstructions string `json:"delivery_instructions"`
	PickUpInstructions   string `json:"pick_up_instructions"`
	TrackingURL          string `json:"tracking_url"`
	TrackingID           string `json:"tracking_id"`
}
