package types

import (
	"fmt"
)

type Money struct {
	Amount   float64 `json:"amount"`
	Currency string  `json:"currency"`
}

func (m *Money) Validate() (error, string, string) {

	if m.Amount < 0 {
		return fmt.Errorf("ammount should not be less then zero"), "Amount", "002"
	}

	return nil, "", ""
}
