package eu.paack.sdk;

import eu.paack.sdk.api.*;
import eu.paack.sdk.api.validator.StatusesRequestValidator;
import eu.paack.sdk.auth.AuthenticationManager;
import eu.paack.sdk.client.ApiClient;
import eu.paack.sdk.client.HttpInteractionFactory;
import eu.paack.sdk.config.*;
import eu.paack.sdk.state.PaackClientStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Paack {

    private PaackClientStatus status = PaackClientStatus.not_initialized;

    @Getter
    private ApiClient apiClient;
    @Getter
    private AuthenticationManager authenticationManager;

    private TrackingApi trackingApi;
    private CoverageApi coverageApi;
    private PodApi podApi;
    private OrderApi orderApi;
    private LabelApi labelApi;
    private RetailerLocationApi retailerLocationApi;

    private final HttpInteractionFactory httpInteractionFactory = new HttpInteractionFactory();

    public Paack(String clientId, String clientSecret, Domain domain) {
        if (clientId == null || clientSecret == null) {
            throw new IllegalArgumentException("Client ID and client secret are required.");
        }
        if (domain == null) {
            throw new IllegalArgumentException("Either staging or production domain must be specified.");
        }

        LocalConfigurationLoader localConfigurationLoader = new LocalConfigurationLoader();
        PaackConfig clientConfig = localConfigurationLoader.getClientConfig(domain);
        clientConfig.getOAuth().setClientId(clientId);
        clientConfig.getOAuth().setClientSecret(clientSecret);
        this.init(clientConfig, domain);
    }

    public void init(PaackConfig clientConfig, Domain domain) {

        this.authenticationManager = new AuthenticationManager(httpInteractionFactory, clientConfig);
        this.apiClient = new ApiClient(httpInteractionFactory, clientConfig);
        apiClient.setAuthenticationManager(this.authenticationManager);

        RemoteConfigurationLoader remoteConfigurationLoader = new RemoteConfigurationLoader(apiClient);
        PaackConfig remoteConfig = remoteConfigurationLoader.getClientConfig(domain);
        this.authenticationManager.setPaackConfig(remoteConfig);
        this.apiClient.setConfig(remoteConfig);

        this.status = PaackClientStatus.initialized;
    }

    public TrackingApi tracking() {
        if (trackingApi == null) {
            trackingApi = TrackingApi.builder().statusesRequestValidator(new StatusesRequestValidator()).apiClient(apiClient).build();
        }
        return trackingApi;
    }

    public CoverageApi coverage() {
        if (coverageApi == null) {
            coverageApi = CoverageApi.builder().apiClient(apiClient).build();
        }
        return coverageApi;
    }

    public PodApi proofOfDelivery() {
        if (podApi == null) {
            podApi = PodApi.builder().apiClient(apiClient).build();
        }
        return podApi;
    }

    public OrderApi order() {
        if (orderApi == null) {
            orderApi = OrderApi.builder().apiClient(apiClient).build();
        }
        return orderApi;
    }

    public LabelApi labeler() {
        if (labelApi == null) {
            labelApi = LabelApi.builder().apiClient(apiClient).build();
        }
        return labelApi;
    }

    public RetailerLocationApi retailerLocation() {
        if (retailerLocationApi == null) {
            retailerLocationApi = RetailerLocationApi.builder().apiClient(apiClient).build();
        }
        return retailerLocationApi;
    }
}

