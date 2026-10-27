package de.emesit.easi18n.internal;

import de.emesit.easi18n.I18nLanguage;

/**
 * Base class your I18n class must inherit from. Only defines the switchLanguage method.
 */
public interface I18nBase {
    
    /**
     * Switches the language to the specified language.
     * @param language the language to switch to
     */
    void switchLanguage(I18nLanguage language);
}
