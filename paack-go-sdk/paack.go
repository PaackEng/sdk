package paack_go_sdk

import (
	"fmt"
	"github.com/PaackEng/paack-go-sdk/logger"
	"github.com/PaackEng/paack-go-sdk/mixin"
	"github.com/PaackEng/paack-go-sdk/resources"
	"github.com/PaackEng/paack-go-sdk/rest"
	"github.com/PaackEng/paack-go-sdk/types/responses"
)

type Paack struct {
	BasePaack
	mixin.Client
	*mixin.PaackConfig
	oauth2Url    string
	TrackingPull rest.TrackingPullApi
	Coverage     rest.CoverageApi
	Pod          rest.PodApi
	Label        rest.LabelApi
	Order        rest.OrderApi
	Retailer     rest.RetailerLocationApi
}

// NewPaack provides an initialized Paack.
func NewPaack(clientId, clientSecret string, domain resources.Domain, logger logger.Logger) (*Paack, *responses.ErrorResponse) {
	url := string(resources.StagingOAuthUrl)
	configUrl := string(resources.StagingConfigUrl)
	audienceList := []string{
		string(domain),
		"https://api.shm.staging.paack.app",
	}

	if domain == resources.ProductionDomain {
		url = string(resources.ProductionOAuthUrl)
		configUrl = string(resources.ProductionConfigUrl)
		audienceList = []string{
			string(domain),
			"https://api.oms.production.paack.app",
		}
	}

	p := Paack{
		BasePaack: *NewBasePaack(clientId, clientSecret, domain, logger),
		Client:    *mixin.NewClient(clientId, clientSecret, url, audienceList, logger),
		oauth2Url: url,
	}

	p.PaackConfig, _ = mixin.NewPaackConfig(string(domain), p.BasePaack.clientId, p.BasePaack.clientSecret, url, configUrl, logger)

	orderAudience := string(resources.StagingDomain)
	retailerAudience := "https://api.shm.staging.paack.app"

	if p.PaackConfig == nil {
		return nil, responses.NewError(fmt.Sprintf("unable to retrieve SDK config"), "", "", logger)
	}

	configDomain := p.PaackConfig.Result.DataConfig.Domain
	podUrl := configDomain.Pod.Staging
	labelUrl := configDomain.Label.Staging
	orderUrl := configDomain.Order.Staging
	coverageUrl := configDomain.Coverage.Staging
	retailerUrl := configDomain.TrackingPull.Staging
	trackingPullUrl := configDomain.TrackingPull.Staging

	if domain == resources.ProductionDomain {
		orderAudience = string(resources.ProductionDomain)
		retailerAudience = "https://api.oms.production.paack.app"

		podUrl = configDomain.Pod.Production
		labelUrl = configDomain.Label.Production
		orderUrl = configDomain.Order.Production
		coverageUrl = configDomain.Coverage.Production
		retailerUrl = configDomain.Coverage.Production
		trackingPullUrl = configDomain.TrackingPull.Production
	}

	configResources := p.PaackConfig.Result.DataConfig.Resources
	p.Pod = *rest.NewPodApi(orderAudience, podUrl, &configResources, *p.GetApiClient(orderAudience), logger)
	p.Label = *rest.NewLabelApi(orderAudience, labelUrl, &configResources, *p.GetApiClient(orderAudience), logger)
	p.Order = *rest.NewOrderApi(orderAudience, orderUrl, &configResources, *p.GetApiClient(orderAudience), logger)
	p.Coverage = *rest.NewCoverageApi(orderAudience, coverageUrl, &configResources, *p.GetApiClient(orderAudience), logger)
	p.Retailer = *rest.NewRetailerLocationApi(retailerAudience, retailerUrl, &configResources, *p.GetApiClient(retailerAudience), logger)
	p.TrackingPull = *rest.NewTrackingPullApi(orderAudience, trackingPullUrl, &configResources, *p.GetApiClient(orderAudience), logger)

	return &p, nil
}
