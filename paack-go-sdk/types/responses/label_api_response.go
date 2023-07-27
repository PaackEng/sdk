package responses

import "bytes"

type LabelCreateResponse interface {
}

type LabelCreateZPLResponse struct {
	IsZpl bool   `json:"is_zpl"`
	Label string `json:"label"`
}

type LabelCreatePDFResponse struct {
	IsZpl bool          `json:"is_zpl"`
	Label *bytes.Reader `json:"label"`
}
