package de.emesit.easi18n;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import de.emesit.easi18n.internal.I18nBase;
import de.emesit.easi18n.internal.I18nBundle;

/**
 * Service interface that gives access to I18nBundle. Either you use I18nBaseBundleService
 * or you im0lement one on your own. The AppLanguageWrapper uses the I18nBundleService to
 * create the proxies.
 * <p>
 * If you implement this I18nBundleService you should override getI18n(I18nLanguage) and 
 * cast the return value to your I18n class.
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
