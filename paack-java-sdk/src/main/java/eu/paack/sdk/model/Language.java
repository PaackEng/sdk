package eu.paack.sdk.model;

public enum Language {
    SPANISH("es"),
    PORTUGUESE("pt"),
    ENGLISH("en"),
    FRENCH("fr"),
    ITALIAN("it"),
    ;

    private final String languageCode;

    Language(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public static Language fromString(String languageCode) {
        for(Language language : values()) {
            if (language.getLanguageCode().equalsIgnoreCase(languageCode)) {
                return language;
            }
        }
        return null;
    }
}
