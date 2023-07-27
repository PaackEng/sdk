package api_client

import (
	"github.com/PaackEng/paack-go-sdk/resources"
	log "github.com/sirupsen/logrus"
	"github.com/stretchr/testify/assert"
	"os"
	"testing"
)

func Init() TokenHandler {
	var clientId = os.Getenv("client_id")
	var clientSecret = os.Getenv("client_secret")
	var oauth2Url = string(resources.StagingOAuthUrl)
	var audience = "https://ggl-stg-gcp-gw"
	var customLogger = log.WithFields(log.Fields{
		"service": "paack-sdk",
	})

	tokenHandler := TokenHandler{
		ClientSecret: clientSecret,
		Oauth2Url:    oauth2Url,
		ClientId:     clientId,
		AudienceList: []string{audience},
		logger:       customLogger,
	}

	return tokenHandler
}

func Test_RetrieveToken(t *testing.T) {
	tokenHandler := Init()
	got, err := tokenHandler.RetrieveToken(tokenHandler.AudienceList[0])

	assert.NotNil(t, got)
	assert.Nil(t, err)
}
