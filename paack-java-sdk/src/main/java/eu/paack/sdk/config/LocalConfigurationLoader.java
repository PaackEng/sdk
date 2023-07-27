package eu.paack.sdk.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import eu.paack.sdk.exceptions.ConfigurationException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class LocalConfigurationLoader implements ConfigurationLoader {
    public final String STAGING_CONFIG_FILE = "paack-config.staging.yml";
    public final String PRODUCTION_CONFIG_FILE = "paack-config.production.yml";
    private final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    public LocalConfigurationLoader() {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        MAPPER.setConfig(MAPPER.getDeserializationConfig().with(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true));
    }

    @Override
    public PaackConfig getClientConfig(Domain domain) {

        String configFileName = domain == Domain.PRODUCTION ? PRODUCTION_CONFIG_FILE : STAGING_CONFIG_FILE;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL configFileResource = classLoader.getResource(configFileName);
        if (configFileResource == null) {
            throw new ConfigurationException("Couldn't find the local configuration file " + configFileName + " in the resources folder.");
        }
        File clientConfigFile = new File(configFileResource.getFile());

        try {
            PaackConfig config = MAPPER.readValue(clientConfigFile, PaackConfig.class);
            if (config.getOAuth() == null) {
                throw new ConfigurationException("Authentication details are mandatory in the configuration.");
            }
            if (config.getOAuth().getAudience() == null || config.getOAuth().getAudience().isEmpty()) {
                throw new ConfigurationException("Configuration server audience cannot be empty.");
            }
            if (config.getOAuth().getIssuerUrl() == null || config.getOAuth().getIssuerUrl().isEmpty()) {
                throw new ConfigurationException("Authentication server url cannot be empty.");
            }
            if (config.getOAuth().getGrantType() == null || config.getOAuth().getGrantType().isEmpty()) {
                throw new ConfigurationException("Authentication grant type cannot be empty.");
            }
            if (config.getMaxRetries() < 0) {
                throw new ConfigurationException("Maximum retries must be larger than 0.");
            }
            if (config.getConfigServer() == null) {
                throw new ConfigurationException("Configuration retrieval server can't be empty.");
            }
            if (config.getConnectTimeout() < -1) {
                throw new ConfigurationException("Connect timeout cannot be smaller than -1, where -1 means infinite.");
            }
            if (config.getSocketTimeout() < -1) {
                throw new ConfigurationException("Socket timeout cannot be smaller than -1, where -1 means infinite.");
            }
            if (config.getConnectionRequestTimeout() < -1) {
                throw new ConfigurationException("Connection request timeout cannot be smaller than -1, where -1 means infinite.");
            }
            if (config.getRetryIntervalInMillis() < 100) {
                throw new ConfigurationException("Retry interval can't be smaller than 100 milliseconds.");
            }
            if (config.getRetryIntervalMultiplicationFactor() < 1d) {
                throw new ConfigurationException("Retry multiplication factor can't be smaller than 1.");
            }

            log.info("Read local Paack Client configuration: {}", config);
            return config;
        } catch (IOException exception) {
            throw new ConfigurationException("Failed to load client configuration, check if the resource or some required property is missing.", exception);
        }
    }
}
