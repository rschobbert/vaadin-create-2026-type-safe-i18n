package de.emesit.easi18n;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

public interface I18nLanguage {
    
    final Comparator<I18nLanguage> CODE_COMPARATOR = Comparator.comparing(I18nLanguage::getCode);
    
    final Comparator<I18nLanguage> CODE_COMPARATOR_NULLSAFE = 
            Comparator.nullsLast(Comparator.comparing(
                    I18nLanguage::getCode, 
                    Comparator.nullsLast(String::compareTo)
            ));

    /**
     * Returns the 2-letter ISO code of this Language.
     * @return the 2-letter ISO code of this Language.
     */
    String getCode();

    /**
     * Returns the index of the this Language, which is just the
     * ordinal of this enum.
     * @return the index of the this Language.
     */
    int getIndex();

    /**
     * Returns true if this Language was defined as mandatory, false otherwise.
     * @return true if this Language was defined as mandatory, false otherwise.
     */
    boolean isMandatory();

    /**
     * Returns the annotationClass this Language corresponds to.
     * @return the annotationClass this Language corresponds to.
     */
    Class<? extends Annotation> getAnnotationClass();

    /**
     * Returns the Locale this Language corresponds to.
     * @return the Locale this Language corresponds to.
     */
    Locale getLocale();

    /**
     * Returns the display name of this Language in its own language.
     * E.g. returns "Deutsch" for Locale.GERMAN, or "English" for Locale.ENGLISH.
     * @return the display name of this Language in its own language.
     */
    default String getName() {
        return getLocale().getDisplayLanguage(getLocale());
    }

    /**
     * Returns the display name of this Language in the specified Locale-language.
     * E.g. returns "German" for Locale.ENGLISH, or "Englisch" for Locale.GERMAN.
     * @return the display name of this Language in the specified Locale-language.
     */
    default String getName(Locale nameLocale) {
        return getLocale().getDisplayLanguage( Objects.requireNonNull(nameLocale, "'nameLocale' must not be null") );
    }

    /**
     * Returns the display name of this Language in the specified Language.
     * E.g. returns "German" for Locale.ENGLISH, or "Englisch" for Locale.GERMAN.
     * @return the display name of this Language in the specified Language.
     */
    default String getName(I18nLanguage nameLanguage) {
        return getLocale().getDisplayLanguage( Objects.requireNonNull(nameLanguage, "'nameLanguage' must not be null").getLocale() );
    }
}
