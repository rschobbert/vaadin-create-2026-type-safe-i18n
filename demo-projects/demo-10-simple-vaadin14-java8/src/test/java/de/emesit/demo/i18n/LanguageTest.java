package de.emesit.demo.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for Language enum")
public class LanguageTest {
    @Nested
    @DisplayName("Tests for of()")
    class OfTests {
        @Test
        @DisplayName("Should return EN when Language is null")
        void shouldReturnENForNull() {
            assertEquals(Language.EN, Language.of(null));
        }
        
        @Test
        @DisplayName("Should return EN for unsupported Locale")
        void shouldReturnENForUnsupportedLocale() {
            assertEquals(Language.EN, Language.of(Locale.JAPANESE));
        }
        
        @Test
        @DisplayName("Should return EN for Locale.ENGLISH")
        void shouldReturnENForEnglishLocale() {
            assertEquals(Language.EN, Language.of(Locale.ENGLISH));
        }
        
        @Test
        @DisplayName("Should return DE for Locale.GERMAN")
        void shouldReturnDEForGermanLocale() {
            assertEquals(Language.DE, Language.of(Locale.GERMAN));
            assertEquals(Locale.GERMAN, Language.of(Locale.GERMAN).getLocale());
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
}
