package responses

type DeliveryVerificationsResponse struct {
	Verifications []struct {
		Data string `json:"data"`
	} `json:"verifications"`
}
