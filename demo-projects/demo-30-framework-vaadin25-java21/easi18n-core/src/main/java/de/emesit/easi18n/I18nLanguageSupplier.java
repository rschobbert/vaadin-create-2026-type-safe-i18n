package de.emesit.easi18n;

import java.util.Locale;
import java.util.function.Supplier;

public interface I18nLanguageSupplier extends Supplier<I18nLanguage[]> {
    /**
     * Returns the I18nLanguage for the specified code (case insensitive) or a default language
     * if no language with that code was found.
     * @param code the ISO 639 alpha-2 (2-letter) language code
     * @return the I18nLanguage for the specified code (case insensitive) or a default language.
     */
    I18nLanguage of(String code);
    
    /**
     * Returns the I18nLanguage for the specified Locale or a default language
     * if no language with that locale was found.
     * 
     * @param locale the Locale of the desired language
     * @return the I18nLanguage for the specified Locale or a default language.
     */
    I18nLanguage of(Locale locale);
    
    /**
     * Returns all possible I18nLanguages.
     */
    I18nLanguage[] get(); // repeat method declaration for plantuml
    
    /**
     * Returns the codes of all possible languages.
     * @return the codes of all possible languages.
     */
    String[] getCodes();
}
