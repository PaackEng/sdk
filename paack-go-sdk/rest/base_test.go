package rest

import (
	"github.com/PaackEng/paack-go-sdk/api_client"
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/mixin"
	"github.com/PaackEng/paack-go-sdk/resources"
	log "github.com/sirupsen/logrus"
	"os"
)

func BaseInit() (*api_client.ApiClient, *mixin.PaackConfig, logger.Logger, string, string) {
	var clientId = os.Getenv("client_id")
	var clientSecret = os.Getenv("client_secret")
	var oauth2Url = string(resources.StagingOAuthUrl)
	var configUrl = string(resources.StagingConfigUrl)
	var orderAudience = string(resources.StagingDomain)
	var retailerAudience = "https://api.shm.staging.paack.app"

	customLogger := log.WithFields(log.Fields{
		"service": "paack-sdk",
	})

	tokenHandler := api_client.NewTokenHandler([]string{orderAudience}, clientId, clientSecret, oauth2Url, customLogger)

	var apiClient = api_client.NewApiClient(tokenHandler, 0, 0, customLogger)
	var config, _ = mixin.NewPaackConfig(orderAudience, clientId, clientSecret, oauth2Url, configUrl, customLogger)

	return apiClient, config, customLogger, orderAudience, retailerAudience
}
