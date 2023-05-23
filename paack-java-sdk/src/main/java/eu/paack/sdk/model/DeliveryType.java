package eu.paack.sdk.model;

public enum DeliveryType {
    DIRECT("direct"),
    REVERSE("reverse");

    private final String deliveryType;
    DeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public static DeliveryType fromString(String deliveryTypeCode) {
        for(DeliveryType deliveryType : values()) {
            if (deliveryType.getDeliveryType().equalsIgnoreCase(deliveryTypeCode)) {
                return deliveryType;
            }
        }
        return null;
    }
}
