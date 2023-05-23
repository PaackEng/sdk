package types

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/resources"
	"time"
)

type ExpectedTs struct {
	End   DateTs `json:"end"`
	Start DateTs `json:"start"`
}

func (e *ExpectedTs) Validate(address Address) (err error, source, code string) {
	err, source, code = e.Start.Validate(address)
	if err != nil {
		return fmt.Errorf("%s", err), "Start." + source, code
	}

	err, source, code = e.End.Validate(address)
	if err != nil {
		return fmt.Errorf("%s", err), "End." + source, code
	}

	start, err := time.Parse(resources.DateTimeFormat, fmt.Sprintf("%s %s", e.Start.Date, e.Start.Time))
	if err != nil {
		return err, "Start.Date", "003"
	}

	end, err := time.Parse(resources.DateTimeFormat, fmt.Sprintf("%s %s", e.End.Date, e.End.Time))
	if err != nil {
		return err, "End.Date", "003"
	}

	if start.After(end) {
		return fmt.Errorf("start date must be before end date"), "Date", "002"
	}

	return
}
