package de.emesit.demo.i18n.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.emesit.demo.i18n.I18n;
import de.emesit.demo.i18n.Language;
import de.emesit.demo.i18n.TranslationDE;
import de.emesit.demo.i18n.TranslationEN;
import de.emesit.demo.i18n.TranslationES;

public class I18nMethodReaderTest {

    // --- 1. SUCCESS TESTS ---

    @Test
    void testReadValidInterface_ShouldSucceed() {
        // Mock interface that follows all rules
        I18nMethodReader reader = new I18nMethodReader(ValidI18nMock.class);
        
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
    void testConsistencyCheck_MissingOptionalLanguage_ShouldApplyFallback() {
        // ES is configured as false for mandatory, so this should pass but log a warning
        I18nMethodReader reader = new I18nMethodReader(MissingOptionalLanguageMock.class);
        
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
        I18nMethodReader reader = new I18nMethodReader(MissingMandatoryLanguageMock.class);
        
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
        I18nMethodReader reader = new I18nMethodReader( (Class)MissingSwitchLanguageMock.class );
        reader.read();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::getMethodSwitchLanguage
        );
        assertTrue(exception.getMessage().contains("switchLanguage() method is missing"));
    }

    @Test
    void testRead_WithWrongSwitchLanguageSignature_ShouldThrowException() {
        I18nMethodReader reader = new I18nMethodReader(WrongSwitchLanguageSignatureMock.class);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::read
        );
        assertTrue(exception.getMessage().contains("wrong parameter type") || exception.getMessage().contains("wrong number of parameterTypes"));
    }

    @Test
    void testRead_MethodWithoutAnyTranslations_ShouldThrowException() {
        I18nMethodReader reader = new I18nMethodReader(NoTranslationsMock.class);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::read
        );
        assertTrue(exception.getMessage().contains("has no translations"));
    }

    @Test
    void testGetMethodSwitchLanguage_BeforeReadCalled_ShouldThrowException() {
        I18nMethodReader reader = new I18nMethodReader(ValidI18nMock.class);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::getMethodSwitchLanguage
        );
        assertTrue(exception.getMessage().contains("read() method has not been called yet"));
    }
    
    @Test
    void testRead_WithOverloadedMethod_ShouldThrowExceptionOnDuplicateKey() {
        // An interface with two methods sharing the exact same name (Key)
        I18nMethodReader reader = new I18nMethodReader(DuplicateKeyMock.class);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::read
        );
        
        // Verify that your exact exception message is thrown
        assertTrue(exception.getMessage().contains("exists twice"));
        assertTrue(exception.getMessage().contains("duplicateMethod"));
    }
    
    @Test
    void testRead_WithWrongSwitchLanguageParamCount_ShouldThrowException() {
        I18nMethodReader reader = new I18nMethodReader(WrongSwitchLanguageParamCountMock.class);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            reader::read
        );
        
        // Verifies that your exact error handling for parameter count triggers
        assertTrue(exception.getMessage().contains("wrong number of parameterTypes"));
        assertTrue(exception.getMessage().contains("expected 1 but was 2"));
    }


    // --- 3. MOCK INTERFACES FOR TEST SCENARIOS ---

    private interface ValidI18nMock extends I18n {
        @Override void switchLanguage(Language language);

        @TranslationDE("Hallo") @TranslationEN("Hello") @TranslationES("Hola")
        String hello();
    }

    private interface MissingOptionalLanguageMock extends I18n {
        @Override void switchLanguage(Language language);

        @TranslationDE("Hallo") @TranslationEN("Hello") // Missing Spanish is ok (since it's optional)
        String onlyMandatory();
    }

    private interface MissingMandatoryLanguageMock extends I18n {
        @Override void switchLanguage(Language language);

        @TranslationDE("Hallo") // Missing English is NOT ok (it's mandatory)
        String missingEnglish();
    }

    private interface MissingSwitchLanguageMock {
        // switchLanguage() is missing
        @TranslationDE("Test") @TranslationEN("Test")
        String test();
    }

    private interface WrongSwitchLanguageSignatureMock extends I18n {
        // Wrong parameter type ('String' instead of 'Language')
        void switchLanguage(String wrongType); 
    }

    private interface NoTranslationsMock extends I18n {
        @Override void switchLanguage(Language language);
        
        String methodWithNoAnnotations(); // Error: No translations available
    }
    
    private interface DuplicateKeyMock extends I18n {
        @Override void switchLanguage(Language language);

        @TranslationDE("First Method") @TranslationEN("First Method")
        String duplicateMethod();

        // Overloaded method -> Same name, different parameters, triggers a duplicate key exception
        @TranslationDE("Second Method") @TranslationEN("Second Method")
        String duplicateMethod(String customParameter);
    }
    
    private interface WrongSwitchLanguageParamCountMock extends I18n {
        // Error: Method has 2 parameters instead of the expected 1 (Language)
        void switchLanguage(Language language, String extraParam); 
    }
}
