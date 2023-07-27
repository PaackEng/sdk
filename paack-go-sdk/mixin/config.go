package mixin

import (
	"encoding/json"
	"fmt"
	"github.com/PaackEng/paack-go-sdk/api_client"
	"github.com/PaackEng/paack-go-sdk/logger"
	"io"
)

type PaackConfig struct {
	Status string `json:"status"`
	Result struct {
		DataConfig DataConfig `json:"data_config"`
		Paths      Paths      `json:"paths"`
		FileConfig FileConfig `json:"file_config"`
		Mappings   Mappings   `json:"mappings"`
	} `json:"result"`
	tokenHandler *api_client.TokenHandler
	logger       logger.Logger
}

type DataConfig struct {
	Audiences Audiences `json:"audiences"`
	Domain    Domain    `json:"domain"`
	Resources Resources `json:"resources"`
}

type Audiences struct {
	Coverage struct {
		Staging    string `json:"staging"`
		Production string `json:"production"`
	} `json:"coverage"`
}

type Domain struct {
	Order struct {
		Staging    string `json:"staging"`
		Production string `json:"production"`
	} `json:"order"`
	TrackingPull struct {
		Production string `json:"production"`
		Staging    string `json:"staging"`
	} `json:"tracking_pull"`
	Label struct {
		Staging    string `json:"staging"`
		Production string `json:"production"`
	} `json:"label"`
	Pod struct {
		Staging    string `json:"staging"`
		Production string `json:"production"`
	} `json:"pod"`
	Coverage struct {
		Staging    string `json:"staging"`
		Production string `json:"production"`
	} `json:"coverage"`
}

type Resources struct {
	Label        string `json:"label"`
	Pod          string `json:"pod"`
	Coverage     string `json:"coverage"`
	Order        string `json:"order"`
	TrackingPull struct {
		StatusList  string `json:"status_list"`
		Translation string `json:"translation"`
		LastStatus  string `json:"last_status"`
	} `json:"tracking_pull"`
}

type Paths struct {
	Pending   string `json:"pending"`
	Errors    string `json:"errors"`
	Processed string `json:"processed"`
}

type FileConfig struct {
	Mapping struct {
	} `json:"mapping"`
	Delimiter string `json:"delimiter"`
	Filenames string `json:"filenames"`
	Filetype  string `json:"filetype"`
}

type Mappings struct {
	Status string `json:"status"`
}

func NewPaackConfig(audience, clientId, clientSecret, oauth2Url, configUrl string, logger logger.Logger) (*PaackConfig, error) {
	tokenHandler := api_client.NewTokenHandler([]string{audience}, clientId, clientSecret, oauth2Url, logger)
	_, err := tokenHandler.RetrieveToken(audience)
	if err != nil {
		return nil, fmt.Errorf("unable to authenticate with the oauth2Url: %s", err)
	}

	conf := PaackConfig{
		tokenHandler: tokenHandler,
		logger:       logger,
	}
	conf, err = conf.getConfig(audience, configUrl)

	return &conf, err
}

// GetConfig - Get SDK Configurations from Paack's API
func (c *PaackConfig) getConfig(audience, configUrl string) (result PaackConfig, err error) {

	apiClient := api_client.NewApiClient(c.tokenHandler, 0, 0, c.logger)

	resp, err := apiClient.Get(configUrl, true, audience)
	if err != nil {
		return result, fmt.Errorf("error making the request to PaackConfigUrl: %s", err)
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return result, fmt.Errorf("configuration was not retreived: %s", resp.Status)
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return result, err
	}

	err = json.Unmarshal(body, &result)
	if err != nil {
		return result, err
	}

	return
}
