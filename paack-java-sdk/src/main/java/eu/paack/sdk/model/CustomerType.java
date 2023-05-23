package eu.paack.sdk.model;

public enum CustomerType {
    STANDARD;

    public static CustomerType fromString(String customerTypeName) {
        for (CustomerType customerType : values()) {
            if (customerType.name().equalsIgnoreCase(customerTypeName)) {
                return customerType;
            }
        }
        return null;
    }
}
