package responses

import (
	"github.com/PaackEng/paack-go-sdk/logger"
)

type Error struct {
	ID     string `json:"id,omitempty"`
	Status string `json:"status,omitempty"`
	Source string `json:"source,omitempty"`
	Code   string `json:"code,omitempty"`
	Detail string `json:"detail,omitempty"`
}

type Data struct {
	Type       string      `json:"type,omitempty"`
	ID         string      `json:"id,omitempty"`
	Attributes interface{} `json:"attributes,omitempty"`
}

type Link struct {
	Href string      `json:"href,omitempty"`
	Meta interface{} `json:"meta,omitempty"`
}

type ErrorResponse struct {
	Data     []Data           `json:"data"`
	Error    []Error          `json:"error"`
	Meta     interface{}      `json:"meta,omitempty"`
	Included []Data           `json:"included,omitempty"`
	Links    *map[string]Link `json:"links"`
}

type ErrorMessageResponse struct {
	Error struct {
		Code    string `json:"code"`
		Message string `json:"message"`
	} `json:"error"`
}

func NewError(detail, code, source string, logger logger.Logger) *ErrorResponse {

	// log error
	logger.Error(detail)

	return &ErrorResponse{
		Error: []Error{
			{
				Detail: detail,
				Code:   code,
				Source: source,
			},
		},
	}
}
