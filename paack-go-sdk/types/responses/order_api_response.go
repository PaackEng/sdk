package responses

import "github.com/PaackEng/paack-go-sdk/types"

type OrderResponse interface {
}

type OrderCreateSuccessResponse struct {
	Success CreateOrderSuccess `json:"success"`
}

type CreateOrderSuccess struct {
	TrackingID string `json:"tracking_id"`
	Status     string `json:"status"`
	Labels     string `json:"labels"`
}

type UpdateOrderResponse struct {
	Success struct {
		ExternalID string `json:"external_id"`
	} `json:"success"`
}

type DeleteOrderResponse struct {
}

type OrderExchangeSuccessResponse struct {
	DirectOrder  CreateOrderSuccess `json:"direct_order"`
	ReverseOrder CreateOrderSuccess `json:"reverse_order"`
}

type GetOrderByIdResponse struct {
	Success types.OrderDTO `json:"success"`
}
