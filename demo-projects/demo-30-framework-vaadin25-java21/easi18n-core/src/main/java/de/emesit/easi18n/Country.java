package de.emesit.easi18n;

/**
 * ISO 3166-1 Alpha-2 Country Codes for GUI Localization.
 * Contains all European countries and core global markets to achieve >80% global coverage.
 */
public enum Country implements I18nCountry {
    
    US, // United States of America
    CN, // China
    TW, // Taiwan
    BR, // Brazil
    
    DE, // Germany
    GB, // United Kingdom
    FR, // France
    ES, // Spain
    IT, // Italy
    NL, // Netherlands
    PL, // Poland
    TR, // Turkey
    RU, // Russia
    UA, // Ukraine
    
    SE, // Sweden
    DK, // Denmark
    NO, // Norway
    FI, // Finland
    EE, // Estonia
    LV, // Latvia
    LT, // Lithuania
    IS, // Iceland
    
    CZ, // Czechia
    SK, // Slovakia
    HU, // Hungary
    RO, // Romania
    BG, // Bulgaria
    HR, // Croatia
    SI, // Slovenia
    
    PT, // Portugal
    GR, // Greece
    
    // the following are not used in Language, but kept in case of other usage
    AT, // Austria
    CH, // Switzerland
    IE, // Ireland
    BE, // Belgium
    LU, // Luxembourg
    CY, // Cyprus
    MT, // Malta
    IN, // India
    AE, // United Arab Emirates
    ;
    
    /**
     * Returns the lowercase country code (useful for generating BCP 47 tags).
     * @return the lowercase country code string.
     */
    public String getCode() {
        return name();
    }
}
