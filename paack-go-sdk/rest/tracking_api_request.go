package rest

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/resources"
	"time"
)

type OrderStatusListRequest struct {
	OrderIds  []string
	StartDate string
	EndDate   string
	Count     int
	Before    string
	After     string
}

func (o *OrderStatusListRequest) Validate() (err error, source, code string) {
	var s, e time.Time

	if len(o.StartDate) > 0 {
		s, err = time.Parse(resources.DateTimeISOFormat, o.StartDate)
		if err != nil {
			return fmt.Errorf("invalid date. Date format must be %s", resources.DateTimeISOFormat), "StartDate", "003"
		}
	}

	if len(o.EndDate) > 0 {
		e, err = time.Parse(resources.DateTimeISOFormat, o.EndDate)
		if err != nil {
			return fmt.Errorf("invalid date. Date format must be %s", resources.DateTimeISOFormat), "EndDate", "003"
		}
	}

	if len(o.StartDate) > 0 && len(o.EndDate) > 0 {
		if s.After(e) {
			return fmt.Errorf("endDate should be after startDate"), "EndDate", "003"
		}
	}

	if o.Count > 0 && (o.Count < 10 || o.Count > 100) {
		return fmt.Errorf("count must be equal or greater then 10 and equal or less then 100"), "Count", "002"
	}

	return
}
