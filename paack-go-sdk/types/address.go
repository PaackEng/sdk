package types

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/resources"
	"regexp"
	"strings"
)

type Address struct {
	City         string `json:"city,omitempty"`
	Country      string `json:"country,omitempty"`
	County       string `json:"county,omitempty"`
	Line1        string `json:"line1,omitempty"`
	Line2        string `json:"line2,omitempty"`
	PostCode     string `json:"post_code,omitempty"`
	Instructions string
}

func (a *Address) Validate() (error, string, string) {

	if len(a.City) == 0 {
		return fmt.Errorf("city field must be provided for address"), "City", "001"
	}

	a.Country = resources.CountryAbreviations[strings.ToUpper(a.Country)]

	if !stringInSlice(a.Country, resources.CountryCode) {
		return fmt.Errorf("country code %s not valid! Possible values: %v", a.Country, resources.CountryCode), "Country", "003"
	}

	if len(a.PostCode) == 0 || len(a.PostCode) < 3 || len(a.PostCode) > 128 {
		return fmt.Errorf("zip code's lenght must be equal or greater then 3 and equal or less then 128 characters"), "PostCode", "002"
	}

	isValid, err := regexp.MatchString(resources.PostalCodePatternPerCountry[a.Country], a.PostCode)
	if err != nil || isValid == false {
		return fmt.Errorf("invalid Zip code %s for country %s", a.PostCode, a.Country), "PostCode", "003"
	}

	if len(a.City) == 0 || len(a.City) < 2 || len(a.City) > 128 {
		return fmt.Errorf("city name's length must be equal or greater then 2 and equal or less then 128 characters"), "City", "002"
	}

	if len(a.Line1) == 0 || len(a.Line1) < 3 || len(a.Line1) > 128 {
		return fmt.Errorf("line1's lenght must be equal or greater then 3 and equal or less then 128 characters"), "Line1", "002"
	}

	if len(a.Line2) == 0 || len(a.Line2) < 3 || len(a.Line2) > 128 {
		return fmt.Errorf("line2's lenght must be equal or greater then 3 and equal or less then 128 characters"), "Line2", "002"
	}

	return nil, "", ""
}

func stringInSlice(a string, list []string) bool {
	for _, b := range list {
		if b == a {
			return true
		}
	}
	return false
}
