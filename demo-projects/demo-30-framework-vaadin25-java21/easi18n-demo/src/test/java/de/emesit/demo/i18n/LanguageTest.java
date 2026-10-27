package de.emesit.demo.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.emesit.easi18n.Language;
import de.emesit.easi18n.annotation.TranslationDE;

@DisplayName("Tests for Language enum")
public class LanguageTest {
    @Nested
    @DisplayName("Tests for of()")
    class OfTests {
        @Test
        @DisplayName("Should return EN when String is null")
        void shouldReturnENForNullLocale() {
            assertEquals(Language.EN, Language.of((Locale)null));
        }
        
        @Test
        @DisplayName("Should return EN when String is null")
        void shouldReturnENForNullString() {
            assertEquals(Language.EN, Language.of((String)null));
        }
        
        @Test
        @DisplayName("Should return EN when String is blank")
        void shouldReturnENForEmptyString() {
            assertEquals(Language.EN, Language.of(""));
        }
        
        
        @Test
        @DisplayName("Should return EN for unsupported Locale")
        void shouldReturnENForUnsupportedLocale() {
            assertEquals(Language.EN, Language.of(Locale.JAPANESE));
        }
        
        @Test
        @DisplayName("Should return EN for unsupported String")
        void shouldReturnENForUnsupportedString() {
            assertEquals(Language.EN, Language.of("ja"));
        }
        
        @Test
        @DisplayName("Should return EN for Locale.ENGLISH")
        void shouldReturnENForEnglishLocale() {
            assertEquals(Language.EN, Language.of(Locale.ENGLISH));
        }
        
        @Test
        @DisplayName("Should return EN for 'en'")
        void shouldReturnENForStringEN() {
            assertEquals(Language.EN, Language.of("en"));
        }
        
        @Test
        @DisplayName("Should return DE for Locale.GERMAN")
        void shouldReturnDEForGermanLocale() {
            assertEquals(Language.DE, Language.of(Locale.GERMAN));
            assertEquals(Language.DE.getLocale(), Language.of(Locale.GERMAN).getLocale());
        }
    }
    
    
    @Nested
    @DisplayName("Tests for getName()")
    class GetNameTests {
        @Test
        @DisplayName("Should return English for EN")
        void shouldReturnEnglishForEN() {
            assertEquals("English", Language.EN.getName());
        }
        @Test
        @DisplayName("Should return Deutsch for DE")
        void shouldReturnDeutschForDE() {
            assertEquals("Deutsch", Language.DE.getName());
        }
        @Test
        @DisplayName("Should return German for DE.getName(EN)")
        void shouldReturnGermanForDE() {
            assertEquals("German", Language.DE.getName(Language.EN));
            assertEquals("German", Language.DE.getName(Locale.ENGLISH));
        }
        @Test
        @DisplayName("Should return Englisch for EN.getName(DE)")
        void shouldReturnEnglischForDE() {
            assertEquals("Englisch", Language.EN.getName(Language.DE));
            assertEquals("Englisch", Language.EN.getName(Locale.GERMAN));
        }
    }
    
    @Nested
    @DisplayName("Tests for basic methods")
    class BasicMethodTests {
        
        @Test
        @DisplayName("Should return count() of 30")
        void shouldReturnCount3() {
            assertEquals(30, Language.count());
        }
        @Test
        @DisplayName("Should return 6 for DE.index()")
        void shouldReturnIndex0ForDE() {
            assertEquals(6, Language.DE.getIndex());
        }
        @Test
        @DisplayName("Should return false for DE.isMandatory()")
        void shouldReturnIsMandatoryTrueForDE() {
            assertEquals(false, Language.DE.isMandatory());
        }
        @Test
        @DisplayName("Should return @TranslationDE for DE.getAnnotationClass()")
        void shouldReturnTranslationDEForAnnotationClassDE() {
            assertEquals(TranslationDE.class, Language.DE.getAnnotationClass());
        }
    }
}
