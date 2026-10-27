package de.emesit.easi18n;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Locale;

import de.emesit.easi18n.annotation.TranslationBG;
import de.emesit.easi18n.annotation.TranslationBR;
import de.emesit.easi18n.annotation.TranslationCZ;
import de.emesit.easi18n.annotation.TranslationDE;
import de.emesit.easi18n.annotation.TranslationDK;
import de.emesit.easi18n.annotation.TranslationEE;
import de.emesit.easi18n.annotation.TranslationEN;
import de.emesit.easi18n.annotation.TranslationES;
import de.emesit.easi18n.annotation.TranslationFI;
import de.emesit.easi18n.annotation.TranslationGR;
import de.emesit.easi18n.annotation.TranslationHR;
import de.emesit.easi18n.annotation.TranslationHU;
import de.emesit.easi18n.annotation.TranslationIS;
import de.emesit.easi18n.annotation.TranslationIT;
import de.emesit.easi18n.annotation.TranslationLT;
import de.emesit.easi18n.annotation.TranslationLV;
import de.emesit.easi18n.annotation.TranslationNL;
import de.emesit.easi18n.annotation.TranslationNO;
import de.emesit.easi18n.annotation.TranslationPL;
import de.emesit.easi18n.annotation.TranslationPT;
import de.emesit.easi18n.annotation.TranslationRO;
import de.emesit.easi18n.annotation.TranslationRU;
import de.emesit.easi18n.annotation.TranslationSE;
import de.emesit.easi18n.annotation.TranslationSI;
import de.emesit.easi18n.annotation.TranslationSK;
import de.emesit.easi18n.annotation.TranslationTR;
import de.emesit.easi18n.annotation.TranslationTW;
import de.emesit.easi18n.annotation.TranslationUA;
import de.emesit.easi18n.annotation.TranslationUS;
import de.emesit.easi18n.annotation.TranslationZH;

/**
 * In this framework demo, Language is an enum implementing the interface I18nLanguage. 
 * But of course we could also implement it using a class.
 * 
 * Though US, TW and BR are countries, they are possible languages to differentiate
 * between english in US and Great Britain / chinese in China and Taiwan /
 * and portuguese in Brazil and Portugal.
 */
public enum Language implements I18nLanguage {
    // --- LANGUAGES with SUBGROUPS (Requires Region differentiation) ---
    EN(true,  TranslationEN.class, Country.GB), // Differentiates between GB (default) and US
    ZH(false, TranslationZH.class, Country.CN), // Differentiates between CN (default) and TW
    PT(false, TranslationPT.class, Country.PT), // Differentiates between PT (default) and BR
    
    // --- for languages with SUBGROUPS we offer a helper Language which is actually a country ---
    US(false, TranslationUS.class, LocalLanguage.of("en", Country.US)), // Differentiates between GB (default) and US
    TW(false, TranslationTW.class, LocalLanguage.of("zh", Country.TW)), // Differentiates between CN (default) and TW
    BR(false, TranslationBR.class, LocalLanguage.of("pt", Country.BR)), // Differentiates between PT (default) and BR
    
    // --- LANGUAGES with UNIQUE countries, so no supgroups needed
    DE(false, TranslationDE.class, Country.DE),
    ES(false, TranslationES.class, Country.ES),
    IT(false, TranslationIT.class, Country.IT),
    PL(false, TranslationPL.class, Country.PL),
    NL(false, TranslationNL.class, Country.NL),
    TR(false, TranslationTR.class, Country.TR),
    RU(false, TranslationRU.class, Country.RU),
    UA(false, TranslationUA.class, Country.UA),
    SE(false, TranslationSE.class, Country.SE),
    DK(false, TranslationDK.class, Country.DK),
    NO(false, TranslationNO.class, Country.NO),
    FI(false, TranslationFI.class, Country.FI),
    CZ(false, TranslationCZ.class, Country.CZ),
    SK(false, TranslationSK.class, Country.SK),
    HU(false, TranslationHU.class, Country.HU),
    RO(false, TranslationRO.class, Country.RO),
    BG(false, TranslationBG.class, Country.BG),
    HR(false, TranslationHR.class, Country.HR),
    SI(false, TranslationSI.class, Country.SI),
    GR(false, TranslationGR.class, Country.GR),
    EE(false, TranslationEE.class, Country.EE),
    LV(false, TranslationLV.class, Country.LV),
    LT(false, TranslationLT.class, Country.LT),
    IS(false, TranslationIS.class, Country.IS),
    ;
    
    record LocalLanguage(Locale locale) {
        static LocalLanguage of(String languageCode, Country country) {
            return new LocalLanguage(Locale.of(languageCode, country.getCode()));
        }
    }

    
    /**
     * Creates a new I18nLanguageSupplier based upon the Language enum.
     * public Method can be called from unittests.
     * @return
     */
    public static I18nLanguageSupplier createLanguageSupplier() {
        return new I18nLanguageSupplier() {

            @Override
            public I18nLanguage[] get() {
                return Language.VALUES;
            }

            @Override
            public I18nLanguage of(String code) {
                return Language.of(code);
            }

            @Override
            public I18nLanguage of(Locale locale) {
                return Language.of(locale);
            }

            @Override
            public String[] getCodes() {
                return Language.CODES;
            }
            
            @Override
            public I18nLanguage getDefault() {
                return Language.DEFAULT;
            }
        };
    }

    public static final Language   DEFAULT = EN;
    public static final Language[] VALUES  = Language.values();
    public static final String[]   CODES   = Arrays.stream(VALUES).map(Language::getCode).toArray(String[]::new);

    /**
     * Returns the number of supported languages.
     * @return the number of supported languages.
     */
    public static int count() {
        return VALUES.length;
    }

    /**
     * Returns the Language for the specified language code.
     * If no Language with the specified code has been defined,
     * returns the default Language, which is EN.
     * 
     * @param code the code (case-insensitive) to get the Language for.
     * @return the Language for the specified language code.
     */
    public static Language of(String code) {
        try {
            return Language.valueOf(code.toUpperCase());
        } catch (Exception e) {
            return DEFAULT; // Standard-Fallback
        }
    }

    /**
     * Returns the Language for the specified Locale.
     * If no Language with the specified locale has been defined,
     * returns the default Language, which is EN.
     * 
     * @param locale the locale to get the Language for.
     * @return the Language for the specified locale.
     */
    public static Language of(Locale locale) {
        return of(locale != null ? locale.getLanguage() : "");
    }

    private final LocalLanguage local;
    private final boolean mandatory;

    private final Class<? extends Annotation> annotationClass;

    /**
     * Constructor for Languages with only one possible Country.
     * @param mandatory
     * @param annotationClass
     * @param country
     */
    private Language(boolean mandatory, Class<? extends Annotation> annotationClass, Country country) {
        this.mandatory = mandatory;
        this.annotationClass = annotationClass;
        this.local = new LocalLanguage(Locale.of(name().toLowerCase(), country.getCode()));
    }
    
    private Language(boolean mandatory, Class<? extends Annotation> annotationClass, LocalLanguage localLanguage) {
        this.mandatory = mandatory;
        this.annotationClass = annotationClass;
        this.local = localLanguage;
    }
    
    @Override
    public String getCode() {
        return local.locale().getLanguage();
    }

    /**
     * Returns the index of the this Language, which is just the
     * ordinal of this enum.
     * @return the index of the this Language.
     */
    @Override
    public int getIndex() {
        return ordinal();
    }

    /**
     * Returns true if this Language was defined as mandatory, false otherwise.
     * @return true if this Language was defined as mandatory, false otherwise.
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Returns the annotationClass this Language corresponds to.
     * @return the annotationClass this Language corresponds to.
     */
    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    /**
     * Returns the Locale this Language corresponds to.
     * @return the Locale this Language corresponds to.
     */
    public Locale getLocale() {
        return local.locale();
    }

    
    @Override
    public String toString() {
        return getCode();
    }
}
