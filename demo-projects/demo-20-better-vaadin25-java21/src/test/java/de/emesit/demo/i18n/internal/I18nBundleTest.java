package de.emesit.demo.i18n.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import de.emesit.demo.I18nConfigAdapter;
import de.emesit.demo.i18n.CustomTranslationService;
import de.emesit.demo.i18n.Language;
import de.emesit.demo.i18n.SimpleTranslationService;
import de.emesit.demo.i18n.TranslationDE;
import de.emesit.demo.i18n.TranslationEN;
import de.emesit.demo.i18n.TranslationES;

@DisplayName("Tests for I18nBundle and Dynamic Proxy Infrastructure")
class I18nBundleTest {
    
    private static I18nBundle createI18nBundle(Class<? extends I18nBase> i18nClass) {
        return createI18nBundle(new I18nMethodReader(new I18nClassSupplier(i18nClass), I18nConfigAdapter.createLanguageSupplier()), null);
    }
    
    private static I18nBundle createI18nBundle(I18nMethodReader i18nMethodReader, CustomTranslationService translationService) {
        return new I18nBundle(i18nMethodReader.i18nClassSupplier, i18nMethodReader, Optional.ofNullable(translationService));
    }

    private I18nMethodReader i18nMethodReader = new I18nMethodReader(new I18nClassSupplier(ValidI18nMock.class), I18nConfigAdapter.createLanguageSupplier());
    private I18nBundle       i18nBundle       = createI18nBundle(ValidI18nMock.class);

    @Nested
    @DisplayName("Tests helper methods")
    class HelperMethodsTests {

        @ParameterizedTest
        @DisplayName("Should correctly convert method names to constant names (SNAKE_CASE)")
        @CsvSource({
            "taskList,            TASK_LIST",
            "updateSqlVersion2,   UPDATE_SQL_VERSION_2",
            "calculateEtlRequest, CALCULATE_ETL_REQUEST",
            "simple,              SIMPLE",
            "already_CONSTANT,    ALREADY_CONSTANT"
        })
        void shouldConvertMethodToConstant(String input, String expected) {
            String result = I18nBundle.methodNameToConstant(input);
            assertEquals(expected, result, () -> "Error converting: " + input);
        }
        
        @ParameterizedTest
        @DisplayName("Should return the input unchanged for null, empty, or blank strings")
        @NullAndEmptySource
        @CsvSource(value = {"'   '"}) // forces test with real spaces
        void shouldReturnInputUnchangedForNullOrEmpty(String input) {
            String result = I18nBundle.methodNameToConstant(input);
            assertEquals(input, result);
        }
    }

    @Nested
    @DisplayName("Tests for Proxy Initialization and Integrity")
    class IntegrityTests {

        @Test
        @DisplayName("Should fail if I18nClassSupplier is null or the class inside")
        void shouldFailIfI18nClassSupplierOrI18nClassIsMissing() {
            
            NullPointerException exception1 = assertThrows(NullPointerException.class, 
                () -> new I18nBundle(null, i18nMethodReader, null)
            );
            NullPointerException exception2 = assertThrows(NullPointerException.class, 
                () -> new I18nBundle(new I18nClassSupplier(null), i18nMethodReader, null)
            );
            assertTrue(exception1.getMessage().contains("must not be null"));
            assertTrue(exception2.getMessage().contains("must not be null"));
        }

        @Test
        @DisplayName("Should successfully load all declared methods into cache map")
        void shouldLoadMethodsIntoMap() {
            assertNotNull(i18nBundle.methodSwitchLanguage, "The internal 'methodSwitchLanguage' must be set");
            
            assertNotNull(i18nBundle.lblsMsgs, "The internal translation map must be initialized");
            
            // Verify that core keys from the I18n interface are loaded
            assertTrue(i18nBundle.lblsMsgs.containsKey("hello")   , "Key 'hello' should be loaded");
        }

        @Test
        @DisplayName("Should provide working proxies for all available languages")
        void shouldCreateProxiesForAllLanguages() {
            for (Language lang : Language.VALUES) {
                I18nBase proxy = i18nBundle.get(lang);
                assertNotNull(proxy, "Proxy instance must not be null for language: " + lang);
            }
        }

        @Test
        @DisplayName("Should provide different proxy instances if get() is called twice")
        void shouldCreateDifferentProxyInstancesForMultipleGetCalls() {
            for (Language lang : Language.VALUES) {
                I18nBase proxy1 = i18nBundle.get(lang);
                I18nBase proxy2 = i18nBundle.get(lang);
                assertNotSame(proxy1, proxy2, "Proxy instance must not be the same for multiple calls to get() with language: " + lang);
            }
        }
    }
    

    @Nested
    @DisplayName("Tests for custom CustomTranslationService in I18nBundle")
    class CustomTranslationTests {

        @Test
        @DisplayName("Should return correct translations for different language indexes")
        void shouldReturnCustomTranslations() {
            SimpleTranslationService simpleTranslationService = new SimpleTranslationService();
            I18nBundle i18nBundle2 = createI18nBundle(i18nMethodReader, simpleTranslationService);
            
            String hello2de = "Auch Hallo";
            String hello2en = "Also hello";
            
            simpleTranslationService.setTranslation("HELLO", Language.DE, hello2de);
            simpleTranslationService.setTranslation("HELLO", Language.EN, hello2en);
            
            // Test German (DE) proxy
            ValidI18nMock deProxy = (ValidI18nMock)i18nBundle2.get(Language.DE);
            assertEquals(hello2de, deProxy.hello());

            // Test English (EN) proxy
            ValidI18nMock enProxy = (ValidI18nMock)i18nBundle2.get(Language.EN);
            assertEquals(hello2en, enProxy.hello());

            // Test Spanish (ES) proxy
            ValidI18nMock esProxy = (ValidI18nMock)i18nBundle2.get(Language.ES);
            assertEquals("Hola", esProxy.hello());
        }
        
        @Test
        @DisplayName("Should return translation keys")
        void shouldReturnTranslationKeys() {
            Set<String> translationKeys = i18nBundle.getTranslationKeys();
            
            assertEquals(Set.of("HELLO", "MSG_FOR"), translationKeys);
        }

        @Test
        @DisplayName("Should support mutable language switching on the same proxy instance")
        void shouldSupportMutableLanguageSwitching() {
            // Get an initial proxy (e.g., German)
            ValidI18nMock proxy = (ValidI18nMock)i18nBundle.get(Language.DE);
            assertEquals("Hallo", proxy.hello());

            // Switch language dynamically to English via the proxy itself
            proxy.switchLanguage(Language.EN);
            assertEquals("Hello", proxy.hello(), "Proxy should update its internal language index dynamically");

            // Switch language dynamically to Spanish
            proxy.switchLanguage(Language.ES);
            assertEquals("Hola", proxy.hello(), "Proxy should adapt to Spanish dynamically");
        }
    }


    @Nested
    @DisplayName("Tests for Type-Safe Translation via Proxy Invoke")
    class ProxyInvokeTests {

        @Test
        @DisplayName("Should return correct translations for different language indexes")
        void shouldReturnCorrectTranslations() {
            // Test German (DE) proxy
            ValidI18nMock deProxy = (ValidI18nMock)i18nBundle.get(Language.DE);
            assertEquals("Hallo", deProxy.hello());
            assertEquals("Nachricht für Harald", deProxy.msgFor("Harald"));

            // Test English (EN) proxy
            ValidI18nMock enProxy = (ValidI18nMock)i18nBundle.get(Language.EN);
            assertEquals("Hello", enProxy.hello());
            assertEquals("Message for Harry", enProxy.msgFor("Harry"));

            // Test Spanish (ES) proxy
            ValidI18nMock esProxy = (ValidI18nMock)i18nBundle.get(Language.ES);
            assertEquals("Hola", esProxy.hello());
            assertEquals("Mensaje para Haroldo", esProxy.msgFor("Haroldo"));
        }
        
        @Test
        @DisplayName("Should return translation keys")
        void shouldReturnTranslationKeys() {
            Set<String> translationKeys = i18nBundle.getTranslationKeys();
            
            assertEquals(Set.of("HELLO", "MSG_FOR"), translationKeys);
        }

        @Test
        @DisplayName("Should support mutable language switching on the same proxy instance")
        void shouldSupportMutableLanguageSwitching() {
            // Get an initial proxy (e.g., German)
            ValidI18nMock proxy = (ValidI18nMock)i18nBundle.get(Language.DE);
            assertEquals("Hallo", proxy.hello());

            // Switch language dynamically to English via the proxy itself
            proxy.switchLanguage(Language.EN);
            assertEquals("Hello", proxy.hello(), "Proxy should update its internal language index dynamically");

            // Switch language dynamically to Spanish
            proxy.switchLanguage(Language.ES);
            assertEquals("Hola", proxy.hello(), "Proxy should adapt to Spanish dynamically");
        }
    }
    
    private interface ValidI18nMock extends I18nBase {
        @TranslationDE("Hallo") @TranslationEN("Hello") @TranslationES("Hola")
        String hello();
        
        @TranslationDE("Nachricht für {0}") @TranslationEN("Message for {0}") @TranslationES("Mensaje para {0}")
        String msgFor(String name);
    }
    
}
