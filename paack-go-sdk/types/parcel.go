package types

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/resources"
	"strings"
)

type Parcel struct {
	WeightUnit       string
	LengthUnit       string
	Barcode          string         `json:"barcode"`
	Height           float64        `json:"height"`
	Length           float64        `json:"length"`
	Type             string         `json:"type"`
	Weight           float64        `json:"weight"`
	Width            float64        `json:"width"`
	VolumetricWeight float64        `json:"volumetric_weight"`
	ParcelDetails    []ExtraDetails `json:"parcel_details"`
}

func (p *Parcel) Validate() (error, string, string) {

	if len(p.Barcode) == 0 {
		return fmt.Errorf("barcode must be definide when creating a parcel object"), "Barcode", "001"
	}

	if len(p.Barcode) > 21 {
		return fmt.Errorf("%s must be <=21 and >= 1 in terms of size", p.Barcode), "Barcode", "002"
	}

	if p.Height != 0 && p.Height < 0 {
		return fmt.Errorf("height must be greater or equal then zero"), "Height", "002"
	}

	if p.Width != 0 && p.Width < 0 {
		return fmt.Errorf("width must be greater or equal then zero"), "Width", "002"
	}

	if p.Weight != 0 && p.Weight < 0 {
		return fmt.Errorf("weight must be greater or equal then zero"), "Weight", "002"
	}

	p.LengthUnit = strings.ToLower(p.LengthUnit)
	p.WeightUnit = strings.ToLower(p.WeightUnit)

	if !stringInSlice(p.WeightUnit, resources.WeightUnits) {
		return fmt.Errorf("invalid parcel's weight unit! Possible values: %v", resources.WeightUnits), "WeightUnits", "003"
	}

	if !stringInSlice(p.LengthUnit, resources.LengthUnits) {
		return fmt.Errorf("invalid parcel's length unit! Possible values: %v", resources.LengthUnits), "LengthUnits", "003"
	}

	_ = p.NormalizeToCm(p.LengthUnit)
	_ = p.NormalizeToKg(p.WeightUnit)

	return nil, "", ""
}

func (p *Parcel) NormalizeToKg(unit string) error {
	if unit == "g" {
		p.Weight = p.Weight / 1000
	} else if unit == "mg" {
		p.Weight = p.Weight / 1000000
	} else if unit == "kg" {

	} else {
		return fmt.Errorf("invalid unit to noramilize parcel's weight -> {%s}", unit)
	}

	return nil
}

func (p *Parcel) NormalizeToCm(unit string) error {
	if unit == "m" {
		p.Height = p.Height * 100
		p.Length = p.Length * 100
		p.Width = p.Width * 100
	} else if unit == "mm" {
		p.Height = p.Height / 10
		p.Length = p.Length / 10
		p.Width = p.Width / 10
	} else if unit == "cm" {

	} else {
		return fmt.Errorf("invalid unit to noramilize parcel's dimensions -> {%s}", unit)
	}

	return nil
}
