package de.emesit.easi18n;

import java.util.List;

/**
 * CustomTranslationService, to integrate a companies custom translations, which will overwrite
 * the default translations in the I18n interface.
 */
public interface CustomTranslationService {
    /**
     * Gets the translation for the specified language and key. Provide the optional args
     * if necessary, or pass null if no arguments are used for that key.
     * Returns the translation for the specified language and key or null if no translation
     * was found.
     * 
     * @param language the language of the translation
     * @param key the translation key
     * @param args the arguments if applicable or null
     * @return the translation for the specified language and key or null.
     */
    default String getTranslation(I18nLanguage language, String key, List<String> args) {
        return getTranslation(language, key, (args != null) ? args.toArray() : null);
    }


    
    /**
     * Gets the translation for the specified language and key. Provide the optional args
     * if necessary, or pass null if no arguments are used for that key.
     * Returns the translation for the specified language and key or null if no translation
     * was found.
     * 
     * @param language the language of the translation
     * @param key the translation key
     * @param args the arguments if applicable or null
     * @return
     */
    default String getTranslation(I18nLanguage language, String key, String[] args) {
        return getTranslation(language, key, (Object[])args);
    }
    
    /**
     * Gets the translation for the specified language and key. Provide the optional args
     * if necessary, or pass null if no arguments are used for that key.
     * Returns the translation for the specified language and key or null if no translation
     * was found.
     * 
     * @param language the language of the translation
     * @param key the translation key
     * @param args the arguments if applicable or null
     * @return
     */
    String getTranslation(I18nLanguage language, String key, Object[] args);
    
    
    /**
     * Gets the translation for the specified language and key without any args.
     * Returns the translation for the specified language and key or null if no translation
     * was found.
     * 
     * @param language the language of the translation
     * @param key the translation key
     * @return the translation for the specified language and key or null.
     */
    default String getTranslation(I18nLanguage language, String key) {
        return getTranslation(language, key, (Object[])null);
    }
}
