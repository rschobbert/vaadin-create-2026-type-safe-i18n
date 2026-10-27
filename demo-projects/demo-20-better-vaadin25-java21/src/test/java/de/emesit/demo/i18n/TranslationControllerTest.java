package de.emesit.demo.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@Import({
    SimpleTranslationService.class
})
@WebMvcTest(TranslationController.class)
class TranslationControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private SimpleTranslationService translationService;
    
    @BeforeEach
    void clearTranslationService() {
        translationService.clear();
    }
    
    @Test
    void testGetLanguages_OK() {
        String expectedText = String.join(", ", Language.CODES);

        mvc.get()
                .uri("/api/translation/languages")
                .assertThat()
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo(expectedText);
    }

    @Test
    void testGetKeys_OK() {
        String expectedText = String.join(", ", translationService.getSortedKeys());
        
        mvc.get()
                .uri("/api/translation/keys")
                .assertThat()
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo(expectedText);
    }

    @Test
    void testGetTranslation_OK() {
        translationService.setTranslation("FIELD_REQUIRED", Language.EN, "foo {0} bar");
        
        mvc.get()
                .uri("/api/translation/get/FIELD_REQUIRED")
                .param("language", "en")
                .param("arg", "MyField")
                .assertThat()
                .hasStatusOk()
                .hasBodyTextEqualTo("foo MyField bar");
    }

    @Test
    void testGetTranslation_NOTFOUND() {
        mvc.get()
                .uri("/api/translation/get/A_KEY_WHICH_DOES_NOT_EXIST")
                .param("language", "en")
                .assertThat()
                .hasStatus4xxClientError();
    }

    @Test
    void testSetTranslation_OK() {
        String oldTranslation = translationService.getTranslation(Language.DE, "FIELD_REQUIRED", List.of());
        String newTranslation = "Feld wird gebraucht";
        mvc.get()
                .uri("/api/translation/set/FIELD_REQUIRED")
                .param("language", "de")
                .param("translation", newTranslation)
                .assertThat()
                .hasStatusOk();
        
        String translation = translationService.getTranslation(Language.DE, "FIELD_REQUIRED", List.of());
        assertEquals(newTranslation, translation);
        assertNotEquals(oldTranslation, newTranslation);
        
        mvc.get()
                .uri("/api/translation/set/NOT_EXISTING")
                .param("language", "de")
                .param("translation", "")
                .assertThat()
                .hasStatus(404);
    }

    @Test
    void testListTranslations_OK() {
        String key = "FIELD_REQUIRED";
        translationService.setTranslation(key, Language.DE, "bla");
        translationService.setTranslation(key, Language.EN, "foo");
        
        String expectedText = """
                FIELD_REQUIRED [de] = [bla]
                FIELD_REQUIRED [en] = [foo]
                """;
        
        mvc.get()
                .uri("/api/translation/list")
                .assertThat()
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo(expectedText);;
        
    }

    @Test
    void testRemoveTranslation_OK() {
        String key = "FIELD_REQUIRED";
        String expectedDE = "bla";
        String expectedEN = "foo";
        translationService.setTranslation(key, Language.DE, expectedDE);
        translationService.setTranslation(key, Language.EN, expectedEN);
        
        mvc.get()
                .uri("/api/translation/delete/"+key)
                .param("language", "de")
                .param("translation", "")
                .assertThat()
                .hasStatusOk();
        
        String translationDE = translationService.getTranslation(Language.DE, key, List.of());
        assertNull(translationDE);
        String translationEN = translationService.getTranslation(Language.EN, key, List.of());
        assertEquals("foo", translationEN);
        
        // try to remove it again, should return 404 not found
        mvc.get()
                .uri("/api/translation/delete/"+key)
                .param("language", "de")
                .param("translation", "")
                .assertThat()
                .hasStatus(404);
    }
}
