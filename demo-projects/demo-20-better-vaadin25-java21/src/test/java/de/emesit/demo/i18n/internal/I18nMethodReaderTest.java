package de.emesit.demo.i18n.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.emesit.demo.I18nConfigAdapter;
import de.emesit.demo.i18n.Language;
import de.emesit.demo.i18n.TranslationDE;
import de.emesit.demo.i18n.TranslationEN;
import de.emesit.demo.i18n.TranslationES;

public class I18nMethodReaderTest {

    // --- 1. SUCCESS TESTS ---

    @Test
    void testReadValidInterface_ShouldSucceed() {
        // Mock interface that follows all rules
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(ValidI18nMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        Map<String, String[]> result = reader.read();
        
        assertNotNull(result);
        assertTrue(result.containsKey("hello"));
        
        // Check indices (DE = 0, EN = 1, ES = 2 based on your Enum definition)
        String[] helloTranslations = result.get("hello");
        assertEquals("Hallo", helloTranslations[Language.DE.getIndex()]);
        assertEquals("Hello", helloTranslations[Language.EN.getIndex()]);
        assertEquals("Hola", helloTranslations[Language.ES.getIndex()]);

        // Validate switchLanguage method
        Method switchMethod = reader.getMethodSwitchLanguage();
        assertNotNull(switchMethod);
        assertEquals("switchLanguage", switchMethod.getName());
    }

    @Test
    void testReadValidUnusualInterface_ShouldSucceed() {
        // Mock interface that follows all rules but has a single-letter method name
        // and a single-capital-letter method name.
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(ValidButUnusualI18nMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        Map<String, String[]> result = reader.read();
        
        assertNotNull(result);
        assertTrue(result.containsKey("a"));
        assertTrue(result.containsKey("B"));
        
        String[] aTranslations = result.get("a");
        assertEquals("A", aTranslations[Language.DE.getIndex()]);
        assertEquals("A", aTranslations[Language.EN.getIndex()]);
        assertNull(aTranslations[Language.ES.getIndex()]);
        
        String[] bTranslations = result.get("B");
        assertEquals("B", bTranslations[Language.DE.getIndex()]);
        assertEquals("B", bTranslations[Language.EN.getIndex()]);
        assertNull(bTranslations[Language.ES.getIndex()]);
    }

    @Test
    void testConsistencyCheck_MissingOptionalLanguage_ShouldApplyFallback() {
        // ES is configured as false for mandatory, so this should pass but log a warning
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(MissingOptionalLanguageMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        Map<String, String[]> result = reader.read();
        String[] translations = result.get("onlyMandatory");
        
        assertEquals("Hallo", translations[Language.DE.getIndex()]);
        assertEquals("Hello", translations[Language.EN.getIndex()]);
        assertNull(translations[Language.ES.getIndex()]); // Optional language is allowed to remain null
    }


    // --- 2. ERROR / FAIL-FAST TESTS ---

    @Test
    void testConsistencyCheck_MissingMandatoryLanguage_ShouldThrowException() {
        // A valid interface where a mandatory translation (EN) is missing
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(MissingMandatoryLanguageMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::read
        );
        assertTrue(exception.getMessage().contains("Missing 1 translation"));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testRead_WithoutSwitchLanguageMethod_ShouldThrowExceptionOnGet() {
        // Trick: We cast the interface "hard" to the expected wildcard
        // to satisfy the compiler
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier((Class)MissingSwitchLanguageMock.class), I18nConfigAdapter.createLanguageSupplier());
        reader.read();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::getMethodSwitchLanguage
        );
        assertTrue(exception.getMessage().contains("switchLanguage() method is missing"));
    }

    @Test
    void testRead_WithWrongSwitchLanguageSignature_ShouldThrowException() {
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(WrongSwitchLanguageSignatureMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::read
        );
        assertTrue(exception.getMessage().contains("wrong parameter type") || exception.getMessage().contains("wrong number of parameterTypes"));
    }

    @Test
    void testRead_MethodWithoutAnyTranslations_ShouldThrowException() {
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(NoTranslationsMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::read
        );
        assertTrue(exception.getMessage().contains("has no translations"));
    }

    @Test
    void testGetMethodSwitchLanguage_BeforeReadCalled_ShouldThrowException() {
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(ValidI18nMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::getMethodSwitchLanguage
        );
        assertTrue(exception.getMessage().contains("read() method has not been called yet"));
    }

    @Test
    void testGetMethodSwitchLanguage_BeforeReadCalled_ShouldThrowException2() {
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(ValidI18nMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::getMethodSwitchLanguage
        );
        assertTrue(exception.getMessage().contains("read() method has not been called yet"));
    }
    
    @Test
    void testRead_WithOverloadedMethod_ShouldThrowExceptionOnDuplicateKey() {
        // An interface with two methods sharing the exact same name (Key)
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(DuplicateKeyMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::read
        );
        
        // Verify that your exact exception message is thrown
        assertTrue(exception.getMessage().contains("exists twice"));
        assertTrue(exception.getMessage().contains("duplicateMethod"));
    }
    
    @Test
    void testRead_WithWrongSwitchLanguageParamCount_ShouldThrowException() {
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(WrongSwitchLanguageParamCountMock.class), I18nConfigAdapter.createLanguageSupplier());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::read
        );
        
        // Verifies that your exact error handling for parameter count triggers
        assertTrue(exception.getMessage().contains("wrong number of parameterTypes"));
        assertTrue(exception.getMessage().contains("expected 1 but was 2"));
    }


    @Test
    void testGetLanguageTranslation_WithValueMethodMissingOnAnnotation_ShouldThrowException() throws Exception {
        
        Class<? extends I18nBase> i18nClass = I18nMockWithSpecialTranslationAnnotation.class;
        Class<TranslationAnnotationWithoutValueMethod> translationAnnotationClass = TranslationAnnotationWithoutValueMethod.class;
        I18nMethodReader reader = new I18nMethodReader(new I18nClassSupplier(i18nClass), I18nConfigAdapter.createLanguageSupplier());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> reader.getLanguageTranslation(
                        TranslationAnnotationWithoutValueMethod.class, 
                        i18nClass.getMethod("test").getAnnotation(translationAnnotationClass), 
                        () -> "error in test"
                )
            );
            assertTrue(exception.getMessage().contains("error in test"));
    }


    // --- 3. MOCK INTERFACES FOR TEST SCENARIOS ---

    static interface ValidI18nMock extends I18nBase {
        @TranslationDE("Hallo") @TranslationEN("Hello") @TranslationES("Hola")
        String hello();
    }

    static interface ValidButUnusualI18nMock extends I18nBase {
        @TranslationDE("A") @TranslationEN("A")
        String a();
        @TranslationDE("B") @TranslationEN("B")
        String B();
    }

    static interface MissingOptionalLanguageMock extends I18nBase {
        @TranslationDE("Hallo") @TranslationEN("Hello") // Missing Spanish is ok (since it's optional)
        String onlyMandatory();
    }

    static interface MissingMandatoryLanguageMock extends I18nBase {
        @TranslationDE("Hallo") // Missing English is NOT ok (it's mandatory)
        String missingEnglish();
    }

    static interface MissingSwitchLanguageMock {
        // switchLanguage() is missing and we do not inherit from I18nBase
        @TranslationDE("Test") @TranslationEN("Test")
        String test();
    }

    static interface WrongSwitchLanguageSignatureMock extends I18nBase {
        // Wrong parameter type ('String' instead of 'Language')
        void switchLanguage(String wrongType); 
    }

    static interface NoTranslationsMock extends I18nBase {
        String methodWithNoAnnotations(); // Error: No translations available
    }
    
    static interface DuplicateKeyMock extends I18nBase {
        @TranslationDE("First Method") @TranslationEN("First Method")
        String duplicateMethod();

        // Overloaded method -> Same name, different parameters, triggers a duplicate key exception
        @TranslationDE("Second Method") @TranslationEN("Second Method")
        String duplicateMethod(String customParameter);
    }
    
    static interface WrongSwitchLanguageParamCountMock extends I18nBase {
        // Error: Method has 2 parameters instead of the expected 1 (Language)
        void switchLanguage(Language language, String extraParam);
    }
    
    
    @Retention(RetentionPolicy.RUNTIME)
    static @interface TranslationAnnotationWithoutValueMethod {
    }
    
    static interface I18nMockWithSpecialTranslationAnnotation extends I18nBase {
        @TranslationDE("DE") @TranslationEN("EN") @TranslationAnnotationWithoutValueMethod()
        String test();
    }
}
