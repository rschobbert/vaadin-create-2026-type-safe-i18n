package de.emesit.demo;

import java.util.Locale;
import java.util.Optional;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.emesit.demo.i18n.I18nBundleService;
import de.emesit.demo.i18n.I18nLanguageSupplier;
import de.emesit.demo.i18n.Language;
import de.emesit.demo.i18n.internal.I18nBase;
import de.emesit.demo.i18n.internal.I18nBundle;
import de.emesit.demo.i18n.internal.I18nClassSupplier;
import de.emesit.demo.i18n.internal.I18nMethodReader;

class I18nConfigTest implements WithAssertions {

    // The ApplicationContextRunner simulates a mini Spring context for each test case
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(UserConfigurations.of(I18nConfig.class, MockDependenciesConfig.class));

    // Dummy classes for testing the type constraints
    static interface MockI18nBase extends I18nBase {}
    static interface WrongI18nWhichDoesNotExtendI18nBase {}

    /**
     * Helper config to satisfy the dependencies injected into the beans
     * (e.g., I18nBundle needed for i18nBundleService)
     */
    @Configuration
    static class MockDependenciesConfig {
        @Bean
        I18nBundle i18nBundle() {
            I18nClassSupplier i18nClassSupplier = new I18nClassSupplier(MockI18nBase.class);
            I18nMethodReader methodReader = new I18nMethodReader(i18nClassSupplier, new I18nConfig().i18nLanguageSupplier());
            return new I18nBundle(i18nClassSupplier, methodReader, Optional.empty()) {}; // Minimal mock implementation
        }
    }

    @Test
    @DisplayName("Should successfully load context and create beans when valid class is provided")
    void shouldCreateBeansWithValidConfiguration() {
        this.contextRunner
                .withPropertyValues("app.i18n.className=" + MockI18nBase.class.getName())
                .run(context -> {
                    // Verify Context loaded successfully
                    assertThat(context).hasNotFailed();

                    // Verify I18nClassSupplier bean creation and content
                    assertThat(context).hasSingleBean(I18nClassSupplier.class);
                    I18nClassSupplier classInfo = context.getBean(I18nClassSupplier.class);
                    // Assuming I18nClassSupplier has a getter or we can verify it's not null
                    assertThat(classInfo).isNotNull();

                    // Verify I18nLanguageSupplier bean
                    assertThat(context).hasSingleBean(I18nLanguageSupplier.class);
                    I18nLanguageSupplier languageSupplier = context.getBean(I18nLanguageSupplier.class);
                    
                    assertThat(languageSupplier.get()).isEqualTo(Language.VALUES);
                    assertThat(languageSupplier.getCodes()).isEqualTo(Language.CODES);
                    assertThat(languageSupplier.of(Locale.GERMAN)).isEqualTo(Language.DE);
                    assertThat(languageSupplier.of(Locale.ENGLISH)).isEqualTo(Language.EN);
                    assertThat(languageSupplier.of("DE")).isEqualTo(Language.DE);
                    assertThat(languageSupplier.of("de")).isEqualTo(Language.DE);
                    assertThat(languageSupplier.of("EN")).isEqualTo(Language.EN);

                    // Verify I18nBundleService bean
                    assertThat(context).hasSingleBean(I18nBundleService.class);
                });
    }

    @Test
    @DisplayName("Should fail with IllegalStateException when 'app.i18n.className' is missing")
    void shouldFailWhenPropertyIsMissing() {
        // We run without setting the property, so i18nClass will be null
        this.contextRunner.run(context -> {
            
            assertThat(context)
                .as("The application context should have failed to start")
                .hasFailed();
            
            assertThat(context.getStartupFailure())
                .isInstanceOf(org.springframework.beans.factory.BeanCreationException.class)
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasStackTraceContaining("No configured I18n class, please set");
        });
    }

    @Test
    @DisplayName("Should fail with IllegalStateException when configured class does not inherit from I18nBase")
    void shouldFailWhenClassDoesNotInheritFromI18nBase() {
        this.contextRunner
                .withPropertyValues("app.i18n.className=" + WrongI18nWhichDoesNotExtendI18nBase.class.getName())
                .run(context -> {
                    
                    assertThat(context)
                        .as("The application context should have failed to start")
                        .hasFailed();
                    
                    assertThat(context.getStartupFailure())
                        .isInstanceOf(org.springframework.beans.factory.BeanCreationException.class)
                        .hasRootCauseInstanceOf(IllegalStateException.class)
                        .hasStackTraceContaining("must inherit from I18nBase");
                });
    }
}
