package rest

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

var trackingPullApi *TrackingPullApi

func TrackingPullApiInit() *TrackingPullApi {
	if trackingPullApi != nil {
		return trackingPullApi
	}

	apiClient, config, customLogger, orderAudience, _ := BaseInit()
	trackingPullApi = NewTrackingPullApi(orderAudience, config.Result.DataConfig.Domain.TrackingPull.Staging, &config.Result.DataConfig.Resources, *apiClient, customLogger)

	return trackingPullApi
}

func Test_OrderStatusGet(t *testing.T) {

	TrackingPullApiInit()
	got, err := trackingPullApi.OrderStatusGet("TESTPAACK-2022100150")

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_EventTranslationGet(t *testing.T) {
	TrackingPullApiInit()
	got, err := trackingPullApi.EventTranslationGet("en")

	assert.NotNil(t, got)
	assert.Nil(t, err)
}

func Test_OrderStatusList(t *testing.T) {
	TrackingPullApiInit()

	request := OrderStatusListRequest{
		OrderIds:  []string{},
		StartDate: "2022-12-12T10:04:05Z",
		EndDate:   "2023-01-01T10:04:05Z",
		Count:     100,
	}

	got, err := trackingPullApi.OrderStatusList(request)

	assert.NotNil(t, got)
	assert.Nil(t, err)
}
