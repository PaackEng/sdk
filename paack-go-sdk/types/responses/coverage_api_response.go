package responses

type CheckCoverageResponse struct {
	CoverageCodes []struct {
		Country      string `json:"country"`
		CoverageCode string `json:"coverage_code"`
	} `json:"coverage_codes"`
	CoverageZones []struct {
		Country      string `json:"country"`
		CoverageZone string `json:"coverage_zone"`
	} `json:"coverage_zones"`
}

type CheckCoveragePostalCodeResponse struct {
	Message string `json:"message"`
}

type CheckCoverageZoneResponse struct {
	Message string `json:"message"`
}
