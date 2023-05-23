package eu.paack.sdk.model;

public enum Country {
    SPAIN("ES", Language.SPANISH),
    FRANCE("FR", Language.FRENCH),
    GREAT_BRITTAN("GB", Language.ENGLISH),
    PORTUGAL("PT", Language.PORTUGUESE),
    ITALY("IT", Language.ITALIAN),
    ;

    private final String countryCode;
    private final Language defaultLanguage;

    Country(String countryCode, Language defaultLanguage) {
        this.countryCode = countryCode;
        this.defaultLanguage = defaultLanguage;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public static Country fromString(String countryCode) {
        for(Country country : values()) {
            if (country.getCountryCode().equalsIgnoreCase(countryCode)) {
                return country;
            }
        }
        return null;
    }
}
