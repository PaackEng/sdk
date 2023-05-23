package types

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/resources"
	"github.com/nyaruka/phonenumbers"
	"regexp"
)

type ContactInfo struct {
	Email          string `json:"email"`
	Phone          string `json:"phone"`
	HasGdprConsent bool   `json:"has_gdpr_consent"`
}

func (c *ContactInfo) Validate(countryCode string) (error, string, string) {

	if len(c.Email) == 0 || len(c.Phone) == 0 {
		return fmt.Errorf("please provide either phone number or email address"), "Email", "001"
	}

	if len(c.Email) != 0 {
		isValid, err := regexp.MatchString(resources.EmailPattern, c.Email)
		if err != nil || isValid == false {
			return fmt.Errorf("please provide a valid email address"), "Email", "003"
		}
	}

	if len(c.Phone) != 0 {
		isValid, err := regexp.MatchString(resources.PhonePattern, c.Phone)
		if err != nil || isValid == false {
			return fmt.Errorf("please provide a valid phone number"), "Phone", "003"
		}

		if len(countryCode) != 0 {
			p, err := phonenumbers.Parse(c.Phone, countryCode)
			if err != nil {
				return fmt.Errorf("unable to parse phone number: %s , err: %s", c.Phone, err), "Phone", "003"
			}
			c.Phone = fmt.Sprintf("+%d%d", p.GetCountryCode(), p.GetNationalNumber())
		}
	}

	return nil, "", ""
}
