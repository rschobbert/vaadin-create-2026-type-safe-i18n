package de.emesit.easi18n.vaadin.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.spring.annotation.UIScope;

import de.emesit.easi18n.I18nAutoConfiguration;
import de.emesit.easi18n.I18nBundleService;
import de.emesit.easi18n.I18nLanguageSupplier;
import de.emesit.easi18n.internal.I18nClassSupplier;
import de.emesit.easi18n.vaadin.AppLanguageWrapper;
import de.emesit.easi18n.vaadin.I18nVaadin;
import de.emesit.easi18n.vaadin.UiLanguageProvider;
import de.emesit.easi18n.vaadin.VaadinCurrentUiLanguageProvider;

@AutoConfiguration
@AutoConfigureAfter(I18nAutoConfiguration.class)
public class VaadinAutoConfiguration {

    /**
     * This BeanPostProcessor inserts the additional I18nVaadin interface
     * into the already created I18nClassSupplier.
     * @return
     */
    @Bean
    static BeanPostProcessor i18nClassSupplierPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof I18nClassSupplier) {
                    I18nClassSupplier supplier = (I18nClassSupplier)bean;
                    supplier.prependInterface(I18nVaadin.class);
                }
                return bean;
            }
        };
    }

    @Bean
    @UIScope
    @ConditionalOnMissingBean(UiLanguageProvider.class)
    UiLanguageProvider uiLanguageProvider(I18nLanguageSupplier languageSupplier) {
        return new VaadinCurrentUiLanguageProvider(languageSupplier);
    }

    @Bean
    @UIScope
    @ConditionalOnMissingBean(AppLanguageWrapper.class)
    AppLanguageWrapper appLanguageWrapper(I18nLanguageSupplier languageSupplier, I18nBundleService i18nBundleService) {
        return new AppLanguageWrapper(languageSupplier, i18nBundleService);
    }
}