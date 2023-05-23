package mixin

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/api_client"
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/resources"
)

type Client struct {
	audienceList []string
	clientId     string
	clientSecret string
	oauth2Url    string
	tokenHandler *api_client.TokenHandler
	apiClient    *api_client.ApiClient
	state        resources.State
	header       map[string][]string
	logger       logger.Logger
}

func NewClient(clientId, clientSecret, oauth2Url string, audienceList []string, logger logger.Logger) *Client {

	c := Client{
		clientId:     clientId,
		clientSecret: clientSecret,
		audienceList: audienceList,
		oauth2Url:    oauth2Url,
		logger:       logger,
	}

	err := c.authorize()
	if err != nil {
		return nil
	}

	return &c
}

func (c *Client) authorize() error {
	c.tokenHandler = api_client.NewTokenHandler(c.audienceList, c.clientId, c.clientSecret, c.oauth2Url, c.logger)

	for _, a := range c.audienceList {
		_, err := c.tokenHandler.RetrieveToken(a)
		if err != nil {
			return fmt.Errorf("unable to authenticate with the oauth2Url: %s", err)
		}
	}

	c.state = resources.Authenticated

	return nil
}

func (c *Client) GetApiClient(audience string) *api_client.ApiClient {
	_, err := c.getHeaders(audience)
	if err != nil {
		return nil
	}

	c.apiClient = api_client.NewApiClient(c.tokenHandler, 0, 0, c.logger)

	return c.apiClient
}

func (c *Client) getHeaders(audience string) (map[string][]string, error) {
	t, err := c.tokenHandler.RetrieveToken(audience)
	if err != nil {
		return nil, fmt.Errorf("unable to retrieve token: %s", err)
	}

	c.header = api_client.DefaultHeaders(t)

	return c.header, nil
}
