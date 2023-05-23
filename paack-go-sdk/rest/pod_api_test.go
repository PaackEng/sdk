package rest

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

var podApi *PodApi

func PodApiInit() *PodApi {
	if podApi != nil {
		return podApi
	}

	apiClient, config, customLogger, orderAudience, _ := BaseInit()
	podApi = NewPodApi(orderAudience, config.Result.DataConfig.Domain.Pod.Staging, &config.Result.DataConfig.Resources, *apiClient, customLogger)

	return podApi
}

func Test_DeliveryVerifications(t *testing.T) {

	PodApiInit()
	got, err := podApi.DeliveryVerifications("order_225ds")

	assert.NotNil(t, got)
	assert.Nil(t, err)
}
