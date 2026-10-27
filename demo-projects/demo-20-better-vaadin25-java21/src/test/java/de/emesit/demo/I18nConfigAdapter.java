package de.emesit.demo;

import de.emesit.demo.i18n.I18nLanguageSupplier;

/**
 * Adapter class to have access to package visible method from I18nConfig.
 */
public class I18nConfigAdapter {
    public static I18nLanguageSupplier createLanguageSupplier() {
        return I18nConfig.createLanguageSupplier();
    }
}
