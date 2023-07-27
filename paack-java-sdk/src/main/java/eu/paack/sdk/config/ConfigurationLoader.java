package eu.paack.sdk.config;

public interface ConfigurationLoader {

    PaackConfig getClientConfig(Domain domain);
}
