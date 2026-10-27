package de.emesit.demo.i18n.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.emesit.demo.i18n.I18n;
import de.emesit.demo.i18n.Language;

@DisplayName("Tests for I18nBundle and Dynamic Proxy Infrastructure")
class I18nBundleTest {

    private I18nBundle i18nBundle;

    @BeforeEach
    void setUp() {
        // Initialize the bundle before each test. 
        // This triggers loadI18nMethods() and consistencyCheck().
        i18nBundle = new I18nBundle();
    }

    @Nested
    @DisplayName("Tests for Proxy Initialization and Integrity")
    class IntegrityTests {

        @Test
        @DisplayName("Should successfully load all declared methods into cache map")
        void shouldLoadMethodsIntoMap() {
            assertNotNull(i18nBundle.lblsMsgs, "The internal translation map must be initialized");
            
            // Verify that core keys from the I18n interface are loaded
            assertTrue(i18nBundle.lblsMsgs.containsKey("taskList"), "Key 'taskList' should be loaded");
            assertTrue(i18nBundle.lblsMsgs.containsKey("description"), "Key 'description' should be loaded");
        }

        @Test
        @DisplayName("Should provide working proxies for all available languages")
        void shouldCreateProxiesForAllLanguages() {
            for (Language lang : Language.VALUES) {
                I18n proxy = i18nBundle.get(lang);
                assertNotNull(proxy, "Proxy instance must not be null for language: " + lang);
            }
        }
    }

    @Nested
    @DisplayName("Tests for Type-Safe Translation via Proxy Invoke")
    class ProxyInvokeTests {

        @Test
        @DisplayName("Should return correct translations for different language indexes")
        void shouldReturnCorrectTranslations() {
            // Test German (DE) proxy
            I18n deProxy = i18nBundle.get(Language.DE);
            assertEquals("Aufgaben Liste", deProxy.taskList());
            assertEquals("Beschreibung", deProxy.description());

            // Test English (EN) proxy
            I18n enProxy = i18nBundle.get(Language.EN);
            assertEquals("Task List", enProxy.taskList());
            assertEquals("Description", enProxy.description());

            // Test Spanish (ES) proxy
            I18n esProxy = i18nBundle.get(Language.ES);
            assertEquals("Lista de Tareas", esProxy.taskList());
            assertEquals("Descripción", esProxy.description());
        }

        @Test
        @DisplayName("Should support mutable language switching on the same proxy instance")
        void shouldSupportMutableLanguageSwitching() {
            // Get an initial proxy (e.g., German)
            I18n proxy = i18nBundle.get(Language.DE);
            assertEquals("Aufgaben Liste", proxy.taskList());

            // Switch language dynamically to English via the proxy itself
            proxy.switchLanguage(Language.EN);
            assertEquals("Task List", proxy.taskList(), "Proxy should update its internal language index dynamically");

            // Switch language dynamically to Spanish
            proxy.switchLanguage(Language.ES);
            assertEquals("Lista de Tareas", proxy.taskList(), "Proxy should adapt to Spanish dynamically");
        }
    }
}
