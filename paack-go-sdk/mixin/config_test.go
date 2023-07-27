package mixin

import (
	"github.com/PaackEng/paack-go-sdk/resources"
	log "github.com/sirupsen/logrus"
	"github.com/stretchr/testify/assert"
	"os"
	"testing"
)

var clientId = os.Getenv("client_id")
var clientSecret = os.Getenv("client_secret")
var oauth2Url = string(resources.StagingOAuthUrl)
var configUrl = string(resources.StagingConfigUrl)
var audience = "https://ggl-stg-gcp-gw"

var customLogger = log.WithFields(log.Fields{
	"service": "paack-sdk",
})

func Test_RetrieveConfig(t *testing.T) {

	got, err := NewPaackConfig(audience, clientId, clientSecret, oauth2Url, configUrl, customLogger)

	assert.NotNil(t, got)
	assert.Nil(t, err)

}
