package de.emesit.easi18n;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import de.emesit.easi18n.internal.I18nBase;
import de.emesit.easi18n.internal.I18nBundle;

/**
 * Custom implementation of the I18nBaseBundleService, used in the AppLanguageWrapper1.
 * <p>
 * Take notice of the overridden method getI18n(Language) which returns the I18n type instead
 * of the I18nBase type.
 * <p>
 * This class is not annotated with springs @Service annotation,
 * since we create the instance in our I18nConfig.
 * 
 * @see de.emesit.demo.I18nConfig
 */
public interface I18nBundleService {

    /**
     * Returns a new dynamic proxy instance from the I18nBundle with the specified language initially set.
     * The language can afterwards be changed in that instance by calling switchLanguage(Language).
     * @param language the language initially set in the I18n dynamic proxy instance.
     * @return a new dynamic proxy instance from the I18nBundle with the specified language initially set.
     * @see I18nBundle#get(Language)
     */
    I18nBase getI18n(I18nLanguage language);

    /**
     * Returns the Set of translation keys from the I18nBundle.
     * @return the Set of translation keys from the I18nBundle.
     * @see I18nBundle#getTranslationKeys()
     */
    SortedSet<String> getTranslationKeys();

    /**
     * Returns the Set of i18n method names from the I18nBundle.
     * @return the Set of i18n method names from the I18nBundle.
     * @see I18nBundle#getI18nMethodNames()
     */
    SortedSet<String> getI18nMethodNames();

    /**
     * Returns an unmodifiable Map of all I18nBundle translations.
     * @return an unmodifiable Map of all I18nBundle translations.
     * @see I18nBundle#getRawTranslations()
     */
    Map<String, String[]> getRawTranslations();

    /**
     * Returns an unmodifiable Set of all I18nLanguages for which translations were found in I18n interface.
     * @return an unmodifiable Set of all I18nLanguages for which translations were found in I18n interface.
     */
    Set<I18nLanguage> getTranslatedLanguages();
}
