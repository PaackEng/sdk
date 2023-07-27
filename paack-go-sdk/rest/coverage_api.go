package rest

import (
	"encoding/json"
	"fmt"
	"github.com/PaackEng/paack-go-sdk/api_client"
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/mixin"
	"github.com/PaackEng/paack-go-sdk/types/responses"
	"io"
	"strconv"
	"strings"
)

/*
CoverageApi - Verifies whether the postcode or postcode zone specified in the query parameter is covered by Paack.
Returns all the covered postcodes and postcode zones if no query parameter is specified.

More details can be found in the Paack API documentation: https://paack.readme.io/reference/coverage
*/
type CoverageApi struct {
	BaseApi
}

func NewCoverageApi(audience, domain string, path *mixin.Resources, client api_client.ApiClient, logger logger.Logger) *CoverageApi {

	return &CoverageApi{
		*NewBaseApi(client, domain, audience, path, logger),
	}
}

/*
CheckCoverage - Returns all covered postcodes and postcode zones.

Returns:

	response (CheckCoverageResponse | ErrorResponse): CoverageResponse({:coverage_codes, :coverage_zones})
*/
func (t *CoverageApi) CheckCoverage() (successResponse *responses.CheckCoverageResponse, errorResponse *responses.ErrorResponse) {
	t.logger.Info("Calling CoverageApi.CheckCoverage method")

	url := fmt.Sprintf("%s%s", t.domain, t.path.Coverage)

	resp, err := t.client.Get(url, true, t.audience)
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", t.logger)
		return
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", body), strconv.Itoa(resp.StatusCode), "", t.logger)
		return
	}

	err = json.Unmarshal(body, &successResponse)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", t.logger)
		return
	}

	return
}

/*
CheckCoveragePostalCode - For a given postcode we need then to check its coverage not only by the complete postcode

Returns:

	True for coverage or False for no coverage.

Args:

	country (str): Country code in ISO 3166 alpha 2 format
	coverage_code (str): Postcode of the location
*/
func (t *CoverageApi) CheckCoveragePostalCode(country, coverageCode string) (successResponse bool, errorResponse *responses.ErrorResponse) {
	t.logger.Info("Calling CoverageApi.CheckCoveragePostalCode method")

	if len(country) == 0 {
		errorResponse = responses.NewError("country cannot be empty", "001", "country", t.logger)
		return
	}

	if len(coverageCode) == 0 {
		errorResponse = responses.NewError("coverageCode cannot be empty", "001", "coverageCode", t.logger)
		return
	}

	country = t.checkCountry(country)
	url := fmt.Sprintf("%s%s?country=%s&coverage_code=%s", t.domain, t.path.Coverage, country, coverageCode)

	resp, err := t.client.Get(url, true, t.audience)
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		if resp.StatusCode == 404 {
			return
		} else {
			errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", resp.Status), strconv.Itoa(resp.StatusCode), "", t.logger)
			return
		}
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", t.logger)
		return
	}

	var response *responses.CheckCoveragePostalCodeResponse
	err = json.Unmarshal(body, &response)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", t.logger)
		return
	}

	if response.Message == "coverage" {
		successResponse = true
	}

	return
}

/*
CheckCoverageZone - For a given coverage_zone we need then to check its coverage not only by the complete coverage_zone

Returns:

	True for coverage or False for no coverage.

Args:

	country (str): Country code in ISO 3166 alpha 2 format
	coverage_zone (str):  Coverage zone
*/
func (t *CoverageApi) CheckCoverageZone(country, coverageZone string) (successResponse bool, errorResponse *responses.ErrorResponse) {
	t.logger.Info("Calling CoverageApi.CheckCoverageZone method")

	if len(country) == 0 {
		errorResponse = responses.NewError("country cannot be empty", "001", "country", t.logger)
		return
	}

	if len(coverageZone) == 0 {
		errorResponse = responses.NewError("coverageZone cannot be empty", "001", "coverageZone", t.logger)
		return
	}

	country = t.checkCountry(country)
	url := fmt.Sprintf("%s%s?country=%s&coverage_zone=%s", t.domain, t.path.Coverage, country, coverageZone)

	resp, err := t.client.Get(url, true, t.audience)
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		if resp.StatusCode == 404 {
			return
		} else {
			errorResponse = responses.NewError(fmt.Sprintf("Received error: %s", resp.Status), strconv.Itoa(resp.StatusCode), "", t.logger)
			return
		}
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while reading response body: %s", err), "", "", t.logger)
		return
	}

	var response *responses.CheckCoverageZoneResponse
	err = json.Unmarshal(body, &response)
	if err != nil {
		errorResponse = responses.NewError(fmt.Sprintf("Error while unmarshalling response body: %s", err), "", "", t.logger)
		return
	}

	if response.Message == "coverage" {
		successResponse = true
	}

	return
}

func (t *CoverageApi) checkCountry(country string) string {
	return strings.ToUpper(country)
}
