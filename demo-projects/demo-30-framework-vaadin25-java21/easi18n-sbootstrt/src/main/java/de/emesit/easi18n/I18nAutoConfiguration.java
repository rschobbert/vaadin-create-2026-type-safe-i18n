package de.emesit.easi18n;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import de.emesit.easi18n.internal.I18nBase;
import de.emesit.easi18n.internal.I18nBaseBundleService;
import de.emesit.easi18n.internal.I18nBundle;
import de.emesit.easi18n.internal.I18nClassSupplier;
import de.emesit.easi18n.internal.I18nMethodReader;

@AutoConfiguration
public class I18nAutoConfiguration {
    @Value("${app.i18n.className:#{null}}")
    private Class<? extends I18nBase> i18nClass;

    @Bean
    @ConditionalOnMissingBean(I18nClassSupplier.class)
    I18nClassSupplier i18nClassSupplier() {
        if (i18nClass == null) {
            throw new IllegalStateException("The property 'app.i18n.className' is missing. "
                    + "Please configure it in your application.properties or define your own I18nClassSupplier Bean.");
        }
        
        if (!I18nBase.class.isAssignableFrom(i18nClass)) {
            throw new IllegalStateException("The configured I18n class " + i18nClass.getName() + " must inherit from I18nBase!");
        }
        
        return new I18nClassSupplier(i18nClass);
    }
    
    @Bean
    @ConditionalOnMissingBean(I18nLanguageSupplier.class)
    I18nLanguageSupplier i18nLanguageSupplier() {
        return Language.createLanguageSupplier();
    }

    @Bean
    @ConditionalOnMissingBean(I18nMethodReader.class)
    I18nMethodReader i18nMethodReader(I18nClassSupplier i18nClassSupplier, I18nLanguageSupplier languageSupplier) {
        return new I18nMethodReader(i18nClassSupplier, languageSupplier);
    }

    @Bean
    @ConditionalOnMissingBean(I18nBundle.class)
    I18nBundle i18nBundle(I18nClassSupplier i18nClassSupplier, I18nMethodReader i18nMethodReader, Optional<CustomTranslationService> optionalTranslationService) {
        return new I18nBundle(i18nClassSupplier, i18nMethodReader, optionalTranslationService);
    }

    @Bean
    @ConditionalOnMissingBean(I18nBundleService.class)
    I18nBundleService i18nBundleService(I18nBundle i18nBundle) {
        return new I18nBaseBundleService(i18nBundle);
    }
}
