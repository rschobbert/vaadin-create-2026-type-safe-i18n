package de.emesit.demo.i18n;

import de.emesit.demo.i18n.internal.I18nBaseBundleService;
import de.emesit.demo.i18n.internal.I18nBundle;

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
public class I18nBundleService extends I18nBaseBundleService {

    public I18nBundleService(I18nBundle i18nBundle) {
        super(i18nBundle);
    }

    @Override
    public I18n getI18n(I18nLanguage language) {
        return (I18n)super.getI18n(language);
    }
}
