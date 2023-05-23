package eu.paack.sdk.config.remote;

import java.util.Map;

public class PaackApiConfig {

    private Map<String, Map<String, String>> domains;
    private Map<String, Object> resources;

    public PaackApiConfig(DataConfig dataConfig) {
        if (dataConfig == null) {
            return;
        }
        this.resources = dataConfig.getResources();
        this.domains = dataConfig.getDomain();
    }

    public String getDomain(String name, String env) {
        return domains.get(name).get(env);
    }

    public String getResource(String resource) {
        return (String) resources.get(resource);
    }

    public String getResource(String resource, String group) {
        Map<String, String> m = (Map<String, String>) resources.get(group);
        return m.get(resource);
    }
}
