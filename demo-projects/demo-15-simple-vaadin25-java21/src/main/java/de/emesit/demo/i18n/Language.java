package de.emesit.demo.i18n;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * In this demo, Language is an enum. But of course we could also implement it
 * differently and use an interface Language, and then have either an enum or a class
 * implementing that interface.
 * <p>
 * But, in order for this demo to not get too complicated, we kept it simple and just implemented
 * it as an enum.
 */
public enum Language {
    DE(true , TranslationDE.class), 
    EN(true , TranslationEN.class), 
    ES(false, TranslationES.class);

    public static final Language   DEFAULT = EN;
    public static final Language[] VALUES = Language.values();
    public static final String[]   NAMES  = Arrays.stream(VALUES).map(it -> it.name().toLowerCase()).toArray(String[]::new);

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

    private final Locale  locale = Locale.forLanguageTag(name().toLowerCase());
    private final boolean mandatory;

    private final Class<? extends Annotation> annotationClass;

    private Language(boolean mandatory, Class<? extends Annotation> annotationClass) {
        this.mandatory       = mandatory;
        this.annotationClass = annotationClass;
    }

    /**
     * Returns the index of the this Language, which is just the
     * ordinal of this enum.
     * @return the index of the this Language.
     */
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
        return locale;
    }

    /**
     * Returns the display name of this Language in its own language.
     * E.g. returns "Deutsch" for Locale.GERMAN, or "English" for Locale.ENGLISH.
     * @return the display name of this Language in its own language.
     */
    public String getName() {
        return locale.getDisplayLanguage(locale);
    }

    /**
     * Returns the display name of this Language in the specified Locale-language.
     * E.g. returns "German" for Locale.ENGLISH, or "Englisch" for Locale.GERMAN.
     * @return the display name of this Language in the specified Locale-language.
     */
    public String getName(Locale nameLocale) {
        return locale.getDisplayLanguage( Objects.requireNonNull(nameLocale, "'nameLocale' must not be null") );
    }

    /**
     * Returns the display name of this Language in the specified Language.
     * E.g. returns "German" for Locale.ENGLISH, or "Englisch" for Locale.GERMAN.
     * @return the display name of this Language in the specified Language.
     */
    public String getName(Language nameLanguage) {
        return locale.getDisplayLanguage( Objects.requireNonNull(nameLanguage, "'nameLanguage' must not be null").getLocale() );
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
