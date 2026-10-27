package de.emesit.demo.i18n;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import de.emesit.demo.I18nConfig;
import de.emesit.demo.JacksonConfig;
import de.emesit.demo.i18n.internal.I18nBundle;
import de.emesit.demo.i18n.internal.I18nMethodReader;

@Import({
    JacksonConfig.class, 
    I18nConfig.class, 
    I18nMethodReader.class,
    I18nBundle.class,
})
@WebMvcTest(I18nController.class)
class I18nControllerTest {
    private static final String BASE_PATH = "/api/i18n";

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private I18nBundleService i18nBundleService;

    @Test
    void testGetLanguages_OK() {
        String expectedText = String.join(", ", Language.CODES);

        mvc.get()
                .uri(BASE_PATH + "/languages")
                .assertThat()
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo(expectedText);
    }

    @Test
    void testGetKeys_OK() {
        String expectedText = String.join(", ", i18nBundleService.getTranslationKeys());
        
        mvc.get()
                .uri(BASE_PATH + "/keys")
                .assertThat()
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo(expectedText);
    }

    @Test
    void testGetMethodNames_OK() {
        String expectedText = String.join(", ", i18nBundleService.getI18nMethodNames());
        
        mvc.get()
                .uri(BASE_PATH + "/methodnames")
                .assertThat()
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo(expectedText);
    }

    @Test
    void testListTranslations_OK() {
        mvc.get()
                .uri(BASE_PATH + "/list")
                .assertThat()
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .body().asString().isNotEmpty();
    }
}
