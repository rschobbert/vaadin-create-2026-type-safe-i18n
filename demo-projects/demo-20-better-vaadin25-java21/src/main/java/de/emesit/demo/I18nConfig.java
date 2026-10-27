package de.emesit.demo;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.emesit.demo.i18n.I18nBundleService;
import de.emesit.demo.i18n.I18nLanguage;
import de.emesit.demo.i18n.I18nLanguageSupplier;
import de.emesit.demo.i18n.Language;
import de.emesit.demo.i18n.internal.I18nBase;
import de.emesit.demo.i18n.internal.I18nBundle;
import de.emesit.demo.i18n.internal.I18nClassSupplier;

@Configuration
public class I18nConfig {
    
    /**
     * Creates a new I18nLanguageSupplier based upon the Language enum.
     * public Method can be called from unittests.
     * @return
     */
    static I18nLanguageSupplier createLanguageSupplier() {
        return new I18nLanguageSupplier() {

            @Override
            public I18nLanguage[] get() {
                return Language.VALUES;
            }

            @Override
            public I18nLanguage of(String code) {
                return Language.of(code);
            }

            @Override
            public I18nLanguage of(Locale locale) {
                return Language.of(locale);
            }

            @Override
            public String[] getCodes() {
                return Language.CODES;
            }
        };
    }
    
    @Value("${app.i18n.className:#{null}}")
    private Class<? extends I18nBase> i18nClass;
    
    @Bean
    I18nClassSupplier i18nClassSupplier() {
        if (i18nClass == null) {
            throw new IllegalStateException("No configured I18n class, please set 'app.i18n.className' in your application.properties!");
        }
        if ( ! I18nBase.class.isAssignableFrom(i18nClass)) {
            throw new IllegalStateException("The configured I18n class " + i18nClass.getName() + " must inherit from I18nBase!");
        }
        return new I18nClassSupplier(i18nClass);
    }
    
    @Bean
    I18nLanguageSupplier i18nLanguageSupplier() {
        return createLanguageSupplier();
    }
    
    @Bean
    I18nBundleService i18nBundleService(I18nBundle i18nBundle) {
        return new I18nBundleService(i18nBundle);
    }
}
