package types

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/resources"
	"strings"
	"time"
)

type DateTs struct {
	Date  string `json:"date"`
	Time  string `json:"time"`
	IsUtc bool   `json:"-"`
}

func (d *DateTs) Validate(address Address) (error, string, string) {

	_, err := time.Parse(resources.DateFormat, d.Date)
	if err != nil {
		return fmt.Errorf("invalid date. Date format must be %s", resources.DateFormat), "Date", "003"

	}

	_, err = time.Parse(resources.TimeFormat, d.Time)
	if err != nil {
		return fmt.Errorf("invalid time. Time format must be %s", resources.TimeFormat), "Time", "003"

	}

	err = d.convertToUtc(address)
	if err != nil {
		return fmt.Errorf("unable to convert values to utc: %s", err), "", ""
	}

	return nil, "", ""
}

func (d *DateTs) getTimezone(address Address) (timezone string) {
	if address.Country == "ES" {
		timezone = resources.TimezonesPerES["peninsula"]
		for _, v := range resources.CanaryIslandsPostcodePrefixes {
			if strings.HasPrefix(address.PostCode, v) {
				timezone = resources.TimezonesPerES["canary_islands"]
				break
			}
		}

	} else {
		timezone = resources.TimezonesPerCountry[address.Country]
	}

	return
}

func (d *DateTs) convertToUtc(address Address) (err error) {
	if d.IsUtc {
		return nil
	}

	// get timezone from resources.TimezonesPerCountry
	timezone := d.getTimezone(address)

	// compute location by timezone
	loc, err := time.LoadLocation(timezone)
	if err != nil {
		return err
	}

	// create time component for location
	t, err := time.ParseInLocation(resources.DateTimeFormat, fmt.Sprintf("%s %s", d.Date, d.Time), loc)
	if err != nil {
		return err
	}

	// convert to UTC
	d.Date = t.UTC().Format(resources.DateFormat)
	d.Time = t.UTC().Format(resources.TimeFormat)
	d.IsUtc = true

	return err
}
