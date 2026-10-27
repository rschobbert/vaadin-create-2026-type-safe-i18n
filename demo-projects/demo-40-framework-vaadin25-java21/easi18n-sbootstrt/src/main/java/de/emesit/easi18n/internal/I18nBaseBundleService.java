package de.emesit.easi18n.internal;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

import de.emesit.easi18n.I18nBundleService;
import de.emesit.easi18n.I18nLanguage;

/**
 * Base class you can inherit from, when implementing your own I18nBundleService (if necessary).
 * Make sure you override the method getI18n(Language) to return your own I18n class.
 */
public class I18nBaseBundleService implements I18nBundleService {
    protected final I18nBundle i18nBundle;

    public I18nBaseBundleService(I18nBundle i18nBundle) {
        this.i18nBundle = Objects.requireNonNull(i18nBundle, "'i18nBundle' must not be null");
    }

    @Override
    public I18nBase getI18n(I18nLanguage language) {
        return i18nBundle.get(language);
    }

    @Override
    public SortedSet<String> getTranslationKeys() {
        return i18nBundle.getTranslationKeys();
    }
    
    @Override
    public SortedSet<String> getI18nMethodNames() {
        return i18nBundle.getI18nMethodNames();
    }
    
    @Override
    public Map<String, String[]> getRawTranslations() {
        return i18nBundle.getRawTranslations();
    }
    
    @Override
    public Set<I18nLanguage> getTranslatedLanguages() {
        return i18nBundle.getTranslatedLanguages();
    }
}
