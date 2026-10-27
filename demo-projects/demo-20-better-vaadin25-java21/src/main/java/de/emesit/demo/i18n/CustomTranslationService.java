package de.emesit.demo.i18n;

import java.util.List;

/**
 * CustomTranslationService, to demonstrate the integration of a companies custom translation service.
 * @see SimpleTranslationService
 * 
 */
public interface CustomTranslationService {
    /**
     * Gets the translation for the specified language and key. Provide the optional arguments
     * if necessary, or pass null if no arguments are used for that key.
     * 
     * @param language
     * @param key
     * @param args
     * @return
     */
    String getTranslation(I18nLanguage language, String key, List<String> args);
    
    /**
     * Gets the translation for the specified language and key. Provide the optional arguments
     * if necessary, or pass null if no arguments are used for that key.
     * 
     * @param language
     * @param key
     * @param args
     * @return
     */
    String getTranslation(I18nLanguage language, String key, String[] args);
    
    /**
     * Gets the translation for the specified language and key. Provide the optional arguments
     * if necessary, or pass null if no arguments are used for that key.
     * 
     * @param language
     * @param key
     * @param args
     * @return
     */
    String getTranslation(I18nLanguage language, String key, Object[] args);
}
