package paack_go_sdk

import (
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/resources"
)

type BasePaack struct {
	clientId     string
	clientSecret string
	domain       resources.Domain
	domainUrl    string
	audience     string
	path         string
	retailerId   string
	logger       logger.Logger
}

// NewBasePaack provides an initialized BasePaack.
func NewBasePaack(clientId, clientSecret string, domain resources.Domain, logger logger.Logger) *BasePaack {
	return &BasePaack{
		clientId:     clientId,
		clientSecret: clientSecret,
		domain:       domain,
		logger:       logger,
	}
}

// SetClientId sets the ClientId for Paack HTTP requests.
func (b *BasePaack) SetClientId(clientId string) {
	b.clientId = clientId
}

// SetClientSecret sets the ClientSecret for Paack HTTP requests.
func (b *BasePaack) SetClientSecret(clientSecret string) {
	b.clientSecret = clientSecret
}

// SetDomain sets the Domain for Paack HTTP requests.
func (b *BasePaack) SetDomain(domain resources.Domain) {
	b.domain = domain
}
