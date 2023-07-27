package api_client

import (
	"bytes"
	"fmt"
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/resources"
	"net/http"
	"time"
)

/*
ApiClient - API connection class. This class works as an interface defining the
methods needed to be implemented on APIClient.
This class is responsible for calling the Paack's API on different audiences/environment.
If a token has expired, this class is responsible for refreshing it and keep the connection alive.

Args:

	headers (Headers): Header object containing the Bearer token
	token_handler (TokenHandler): token handler object, needed for when the token has expired
	connection_timeout (float): user can define a connection timeout of default will be used (default is 60.05)
	read_timeout (float): user can define a read timeout of default will be used (default is 60.0)
*/
type ApiClient struct {
	Header            map[string][]string `json:"headers"`
	TokenHandler      *TokenHandler       `json:"token_handler"`
	ConnectionTimeout float32             `json:"connection_timeout"`
	ReadTimeout       float32             `json:"read_timeout"`
	HTTPClient        *http.Client
	logger            logger.Logger
}

func NewApiClient(tokenHandler *TokenHandler, connectionTimeout, readTimeout float32, logger logger.Logger) *ApiClient {

	if connectionTimeout == 0 {
		connectionTimeout = resources.ConnectionTimeout
	}

	if readTimeout == 0 {
		readTimeout = resources.ReadTimeout
	}

	return &ApiClient{
		TokenHandler:      tokenHandler,
		ConnectionTimeout: connectionTimeout,
		ReadTimeout:       readTimeout,
		HTTPClient:        defaultHTTPClient(time.Duration(connectionTimeout)),
		logger:            logger,
	}
}

func defaultHTTPClient(connectionTimeout time.Duration) *http.Client {
	return &http.Client{
		CheckRedirect: func(req *http.Request, via []*http.Request) error {
			return http.ErrUseLastResponse
		},
		Timeout: time.Second * connectionTimeout,
	}
}

func (c *ApiClient) SetConnectionTimeout(timeout time.Duration) {
	if c.HTTPClient == nil {
		c.HTTPClient = defaultHTTPClient(timeout)
	}
	c.ConnectionTimeout = float32(timeout)
	c.HTTPClient.Timeout = timeout
}

func (c *ApiClient) Post(url string, body []byte, refreshToken bool, audience string) (*http.Response, error) {
	c.logger.Info(fmt.Sprintf("Sending request: %s %s %s", http.MethodPost, url, string(body)))

	request, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(body))
	if err != nil {
		return nil, fmt.Errorf("error creating the request to %s", url)
	}

	var token *CachedToken
	if refreshToken {
		token, err = c.TokenHandler.RetrieveToken(audience)
		if err != nil {
			return nil, err
		}
	}
	request.Header = DefaultHeaders(token)

	return c.doRequestWithRetry(refreshToken, audience, request)
}

func (c *ApiClient) Put(url string, body []byte, refreshToken bool, audience string) (*http.Response, error) {
	c.logger.Info(fmt.Sprintf("Sending request: %s %s %s", http.MethodPut, url, string(body)))

	request, err := http.NewRequest(http.MethodPut, url, bytes.NewBuffer(body))
	if err != nil {
		return nil, fmt.Errorf("error creating the request to %s", url)
	}

	var token *CachedToken
	if refreshToken {
		token, err = c.TokenHandler.RetrieveToken(audience)
		if err != nil {
			return nil, err
		}
	}
	request.Header = DefaultHeaders(token)

	return c.doRequestWithRetry(refreshToken, audience, request)
}

func (c *ApiClient) Patch(url string, body []byte, refreshToken bool, audience string) (*http.Response, error) {
	c.logger.Info(fmt.Sprintf("Sending request: %s %s %s", http.MethodPatch, url, string(body)))

	request, err := http.NewRequest(http.MethodPatch, url, bytes.NewBuffer(body))
	if err != nil {
		return nil, fmt.Errorf("error creating the request to %s", url)
	}

	var token *CachedToken
	if refreshToken {
		token, err = c.TokenHandler.RetrieveToken(audience)
		if err != nil {
			return nil, err
		}
	}
	request.Header = DefaultHeaders(token)

	return c.doRequestWithRetry(refreshToken, audience, request)
}

func (c *ApiClient) Get(url string, refreshToken bool, audience string) (*http.Response, error) {
	c.logger.Info(fmt.Sprintf("Sending request: %s %s", http.MethodGet, url))

	request, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, fmt.Errorf("error creating the request to %s", url)
	}

	return c.doRequestWithRetry(refreshToken, audience, request)
}

func (c *ApiClient) Delete(url string, refreshToken bool, audience string) (*http.Response, error) {
	c.logger.Info(fmt.Sprintf("Sending request: %s %s", http.MethodDelete, url))

	request, err := http.NewRequest(http.MethodDelete, url, nil)
	if err != nil {
		return nil, fmt.Errorf("error creating the request to %s", url)
	}

	var token *CachedToken
	if refreshToken {
		token, err = c.TokenHandler.RetrieveToken(audience)
		if err != nil {
			return nil, err
		}
	}
	request.Header = DefaultHeaders(token)

	return c.doRequestWithRetry(refreshToken, audience, request)
}

func (c *ApiClient) doRequestWithRetry(refreshToken bool, audience string, request *http.Request) (*http.Response, error) {
	var token *CachedToken
	var err error

	for i := 0; i < resources.FailedRequestsAttemptLimit; i++ {
		if refreshToken {
			token, err = c.TokenHandler.RetrieveToken(audience)
			if err != nil {
				return nil, err
			}
		}

		request.Header = DefaultHeaders(token)

		resp, err := c.HTTPClient.Do(request)
		if resp != nil && resp.StatusCode != 401 {
			return resp, err
		}

		// token is invalid, will be deleted
		c.TokenHandler.DeleteToken(audience)
		time.Sleep(3 * time.Second)
	}

	return c.HTTPClient.Do(request)
}
