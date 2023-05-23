package types

import "C"
import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/resources"
	"strings"
)

type Customer struct {
	ContactInfo
	FirstName       string         `json:"first_name"`
	LastName        string         `json:"last_name"`
	Address         Address        `json:"address"`
	Language        string         `json:"language"`
	CustomerType    string         `json:"customer_type"`
	CustomerDetails []ExtraDetails `json:"customer_details"`
}

func (c *Customer) Validate(validateOnlyCustomerName bool) (error, string, string) {

	if len(c.FirstName) == 0 {
		return fmt.Errorf("customer's first name must be defined"), "FirstName", "001"
	}

	if len(c.LastName) == 0 {
		return fmt.Errorf("customer's last name must be defined"), "LastName", "001"
	}

	if validateOnlyCustomerName {
		return nil, "", ""
	}

	c.Language = strings.ToLower(c.Language)

	if !stringInSlice(c.Language, resources.CustomerLanguage) {
		return fmt.Errorf("language %s not valid! Possible values: %v", c.Language, resources.CountryCode), "Language", "003"
	}

	err, source, code := c.ContactInfo.Validate(c.Address.Country)
	if err != nil {
		return fmt.Errorf("%s", err), "ContactInfo." + source, code
	}

	return nil, "", ""
}
