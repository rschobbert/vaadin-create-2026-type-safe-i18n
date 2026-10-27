package de.emesit.easi18n.vaadin;

import de.emesit.easi18n.I18nLanguage;

@FunctionalInterface
public interface UiLanguageProvider {
    /**
     * Returns the current language of the user.
     */
    I18nLanguage getCurrentUiLanguage();
}

