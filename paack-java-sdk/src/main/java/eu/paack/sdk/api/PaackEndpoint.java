package eu.paack.sdk.api;

public enum PaackEndpoint {
    config("config", null),
    order("order", null),
    tracking_status("last_status", "tracking_pull"),
    tracking_history("status_list", "tracking_pull"),
    tracking_translation("translation", "tracking_pull"),
    coverage("coverage", null),
    pod("pod", null),
    label("label", null),
    retailer("retailer_location", null);

    private final String name;
    private final String group;

    PaackEndpoint(String name, String group) {
        this.name = name;
        this.group = group;
    }

    public String getName() {
        return this.name;
    }

    public String getGroup() {
        return this.group;
    }

    public boolean isGroup() {
        return group != null && !this.group.isEmpty();
    }
}
