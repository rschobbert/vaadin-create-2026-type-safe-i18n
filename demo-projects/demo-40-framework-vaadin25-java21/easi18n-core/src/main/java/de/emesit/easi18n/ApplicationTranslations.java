package de.emesit.easi18n;

import java.util.List;

/**
 * Use the ApplicationTranslations interface, to integrate custom translations
 * which come from an application internal (usually read-only) source.
 * <p>
 * The order of overwriting is: 
 *   {@code @Translation, ApplicationTranslations, CustomTranslationService}
 * <p>
 * That means translations in ApplicationTranslations overwrite the annotations,
 * translations from CustomTranslationService overwrite the other two.
 * Remember that method names in ALL I18n interfaces have to be unique. See
 * also comment in I18nClassSupplier.
 * <p>
 * This comes mainly from the fact, that I wanted a spring-independant way of
 * overwriting translations in submodules (e.g. the I18nVaadin interface which
 * has a lblAppLanguage() method and translations annotated. But since it is a 
 * framework, applications using the framework often want different translations).
 * <p>
 * I didn't want to use multiple CustomTranslationService(s) as this would be too heavy
 * and confusing. Therefore this interface gives you the possibility, to overwrite
 * framework internal translations, or to provide (a small number of) translations 
 * directly in you source code.
 * <p>
 * In spring you can place application translations in your application.properties.
 */
public interface ApplicationTranslations {
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
     * @return the translation for the specified language and key or null.
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
     * @return the translation for the specified language and key or null.
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
