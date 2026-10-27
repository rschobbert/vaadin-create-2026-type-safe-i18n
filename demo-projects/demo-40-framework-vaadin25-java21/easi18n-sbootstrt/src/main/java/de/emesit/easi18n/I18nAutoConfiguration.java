package de.emesit.easi18n;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import de.emesit.easi18n.internal.I18nBase;
import de.emesit.easi18n.internal.I18nBaseBundleService;
import de.emesit.easi18n.internal.I18nBundle;
import de.emesit.easi18n.internal.I18nClassSupplier;
import de.emesit.easi18n.internal.I18nMethodReader;
import de.emesit.easi18n.internal.I18nMethodTranslationReader;
import de.emesit.easi18n.internal.MethodOverrideMode;

@AutoConfiguration
public class I18nAutoConfiguration {
    private static final String PROP_I18N_CLASS_NAME = "easi18n.i18nClass";
    private static final String PROP_METHOD_OVRM     = "easi18n.methodOverrideMode";
    private static final String PROP_I18N_PROXY_IFCS = "easi18n.i18nProxyInterfaces";
    
    @Value("${"+PROP_I18N_CLASS_NAME+":#{null}}")
    private Class<? extends I18nBase> i18nClass;

    @Value("${"+PROP_METHOD_OVRM+":FAILS}")
    private String overrideModeProperty;
    
    @Value("${"+PROP_I18N_PROXY_IFCS+":#{null}}")
    private List<Class<?>> i18nProxyInterfaces;

    @Bean
    @ConditionalOnMissingBean(ApplicationTranslations.class)
    ApplicationTranslations applicationTranslations(Environment environment) {
        return new SpringPropertiesApplicationTranslations(environment);
    }
    
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
        
        MethodOverrideMode mode;
        try {
            mode = MethodOverrideMode.valueOf(overrideModeProperty.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown MethodOverrideMode '" + overrideModeProperty + "'. Allowed values are: "+Arrays.toString(MethodOverrideMode.values()));
        }

        return new I18nClassSupplier(i18nClass, i18nProxyInterfaces, mode);
    }
    
    @Bean
    @ConditionalOnMissingBean(I18nLanguageSupplier.class)
    I18nLanguageSupplier i18nLanguageSupplier() {
        return Language.createLanguageSupplier();
    }

    @Bean
    @ConditionalOnMissingBean(I18nMethodTranslationReader.class)
    I18nMethodTranslationReader i18nMethodTranslationReader(ApplicationTranslations applicationTranslations) {
        return new I18nMethodTranslationReader(applicationTranslations);
    }
    
    @Bean
    @ConditionalOnMissingBean(I18nMethodReader.class)
    I18nMethodReader i18nMethodReader(I18nClassSupplier i18nClassSupplier, I18nLanguageSupplier languageSupplier, I18nMethodTranslationReader translationReader) {
        return new I18nMethodReader(i18nClassSupplier, languageSupplier, translationReader);
    }

    @Bean
    @ConditionalOnMissingBean(I18nBundle.class)
    I18nBundle i18nBundle(I18nClassSupplier i18nClassSupplier, I18nMethodReader i18nMethodReader, ApplicationTranslations applicationTranslations, Optional<CustomTranslationService> optionalTranslationService) {
        return new I18nBundle(i18nClassSupplier, i18nMethodReader, applicationTranslations, optionalTranslationService);
    }

    @Bean
    @ConditionalOnMissingBean(I18nBundleService.class)
    I18nBundleService i18nBundleService(I18nBundle i18nBundle) {
        return new I18nBaseBundleService(i18nBundle);
    }
}
