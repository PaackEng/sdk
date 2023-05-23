package eu.paack.sdk.config;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.paack.sdk.api.PaackEndpoint;
import eu.paack.sdk.api.model.response.PaackConfigsResponse;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.client.ApiClient;
import eu.paack.sdk.config.remote.DataConfig;
import eu.paack.sdk.exceptions.ApiException;
import eu.paack.sdk.exceptions.ConfigurationException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RemoteConfigurationLoader implements ConfigurationLoader {

    private final ApiClient apiClient;

    public RemoteConfigurationLoader(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public PaackConfig getClientConfig(Domain domain) {
        try {
            PaackResponse<PaackConfigsResponse, Error> response = apiClient.invokeAPI(
                    PaackEndpoint.config,
                    "GET",
                    null,
                    null,
                    null,
                    new TypeReference<PaackConfigsResponse>() {});
            log.info("Received the following configuration from API: {}", response.toString());
            if (response.getData() == null || response.getData().getResult() == null || response.getData().getResult().getDataConfig() == null) {
                throw new ConfigurationException("Received an invalid configuration from the Configuration API. Please contact Paack with the logged response.");
            }

            PaackConfig currentConfig = apiClient.getConfig();
            DataConfig remoteConfig = response.getData().getResult().getDataConfig();

            currentConfig.setAudiences(mergeMaps(flattenConfigToCurrentEnvironment(remoteConfig.getAudiences(), domain), currentConfig.getAudiences()));
            currentConfig.setDomains(mergeMaps(flattenConfigToCurrentEnvironment(remoteConfig.getDomain(), domain), currentConfig.getDomains()));
            currentConfig.setResources(mergeMaps(remoteConfig.getResources(), currentConfig.getResources()));

            return currentConfig;
        } catch (ApiException e) {
            throw new ConfigurationException("Received an invalid configuration from the Configuration API. Please contact Paack with the exception.", e);
        }
    }

    private Map<String, String> flattenConfigToCurrentEnvironment(Map<String, Map<String, String>> config, Domain domain) {
        if (config == null || domain == null) {
            return Collections.emptyMap();
        }
        String environment = domain.name().toLowerCase();

        return config.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().get(environment)));
    }

    private <T> Map<String, T> mergeMaps(Map<String, T> map1, Map<String, T> map2) {
        Map<String, T> merged = new HashMap<>(map1);
        map2.forEach((key, value) -> {
            if (value == null) {
                return;
            }
            merged.merge(key, value, (oldValue, newValue) -> {
                if(oldValue instanceof Map && newValue instanceof Map) {
                    return (T) mergeMaps((Map<String, String>) oldValue, (Map<String, String>) newValue);
                }
                if (oldValue instanceof String && newValue instanceof String) {
                    return ((String) newValue).isEmpty() ? oldValue : newValue;
                }
                return newValue;
            });
        });

        return merged;
    }
}
