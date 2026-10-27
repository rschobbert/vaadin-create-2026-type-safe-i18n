package de.emesit.demo.i18n.internal;

import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;

import de.emesit.demo.i18n.I18nLanguage;

/**
 * Base class you usually inherit from, when implementing you I18nBundleService.
 * Make sure you override the method getI18n(Language) to return your own I18n class.
 */
public class I18nBaseBundleService {
    protected final I18nBundle i18nBundle;

    public I18nBaseBundleService(I18nBundle i18nBundle) {
        this.i18nBundle = Objects.requireNonNull(i18nBundle, "'i18nBundle' must not be null");
    }

    /**
     * Returns a new dynamic proxy instance from the I18nBundle with the specified language initially set.
     * The language can afterwards be changed in that instance by calling switchLanguage(Language).
     * @param language the language initially set in the I18n dynamic proxy instance.
     * @return a new dynamic proxy instance from the I18nBundle with the specified language initially set.
     * @see I18nBundle#get(Language)
     */
    public I18nBase getI18n(I18nLanguage language) {
        return i18nBundle.get(language);
    }

    /**
     * Returns the Set of translation keys from the I18nBundle.
     * @return the Set of translation keys from the I18nBundle.
     * @see I18nBundle#getTranslationKeys()
     */
    public SortedSet<String> getTranslationKeys() {
        return i18nBundle.getTranslationKeys();
    }
    
    /**
     * Returns the Set of i18n method names from the I18nBundle.
     * @return the Set of i18n method names from the I18nBundle.
     * @see I18nBundle#getI18nMethodNames()
     */
    public SortedSet<String> getI18nMethodNames() {
        return i18nBundle.getI18nMethodNames();
    }
    
    /**
     * Returns an unmodifiable Map of all I18nBundle translations.
     * @return an unmodifiable Map of all I18nBundle translations.
     * @see I18nBundle#getRawTranslations()
     */
    public Map<String, String[]> getRawTranslations() {
        return i18nBundle.getRawTranslations();
    }
}
