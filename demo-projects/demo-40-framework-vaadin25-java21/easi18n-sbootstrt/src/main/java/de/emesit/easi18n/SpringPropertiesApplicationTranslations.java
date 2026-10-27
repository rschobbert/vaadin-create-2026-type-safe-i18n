package de.emesit.easi18n;

import java.text.MessageFormat;

import org.springframework.core.env.Environment;

public class SpringPropertiesApplicationTranslations implements ApplicationTranslations {
    private static final String PREFIX = "easi18n.translation.";

    private final Environment environment;

    public SpringPropertiesApplicationTranslations(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getTranslation(I18nLanguage language, String key, Object[] args) {
        // Such-Schema: 
        // easi18n.translation.LBL_APP_LANGUAGE.en=Language
        // easi18n.translation.LBL_APP_LANGUAGE.de=Sprache
        String propertyKey = PREFIX + key+"." + language.getCode();
        String translation = environment.getProperty(propertyKey);
        
        // optional fallback without language: easi18n.translation.LBL_APP_LANGUAGE
        if (translation == null) {
            translation = environment.getProperty(PREFIX + key);
        }

        if (translation != null && args != null && args.length > 0) {
            return MessageFormat.format(translation, args);
        }
        return translation;
    }
}