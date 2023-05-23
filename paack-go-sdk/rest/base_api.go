package rest

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/api_client"
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/mixin"
)

type BaseApi struct {
	client   api_client.ApiClient
	path     *mixin.Resources
	logger   logger.Logger
	domain   string
	audience string
}

func NewBaseApi(client api_client.ApiClient, domain, audience string, path *mixin.Resources, logger logger.Logger) *BaseApi {
	return &BaseApi{
		client:   client,
		domain:   domain,
		path:     path,
		logger:   logger,
		audience: audience,
	}
}

func (b *BaseApi) GetUrl() string {
	return fmt.Sprintf("%s%s", b.domain, b.path)
}
