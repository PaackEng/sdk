package rest

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

var coverageApi *CoverageApi

func InitCoverageApi() *CoverageApi {

	if coverageApi != nil {
		return coverageApi
	}

	apiClient, config, customLogger, orderAudience, _ := BaseInit()

	coverageApi = NewCoverageApi(orderAudience, config.Result.DataConfig.Domain.Coverage.Staging, &config.Result.DataConfig.Resources, *apiClient, customLogger)

	return coverageApi
}

func Test_CheckCoverage(t *testing.T) {

	InitCoverageApi()
	got, err := coverageApi.CheckCoverage()

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_CheckCoveragePostalCode(t *testing.T) {

	InitCoverageApi()
	got, err := coverageApi.CheckCoveragePostalCode("Es", "06430")

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_CheckCoveragePostalCodeFail(t *testing.T) {

	InitCoverageApi()
	_, err := coverageApi.CheckCoveragePostalCode("abc", "06430")

	assert.NotNil(t, err)
}

func Test_CheckCoverageZone(t *testing.T) {

	InitCoverageApi()
	got, err := coverageApi.CheckCoverageZone("pt", "2680")

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_CheckCoverageZoneFail(t *testing.T) {

	InitCoverageApi()
	_, err := coverageApi.CheckCoverageZone("abc", "2680")

	assert.NotNil(t, err)
}
