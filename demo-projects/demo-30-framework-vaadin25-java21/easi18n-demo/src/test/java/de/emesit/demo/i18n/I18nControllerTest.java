package de.emesit.demo.i18n;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import de.emesit.demo.JacksonConfig;
import de.emesit.easi18n.I18nAutoConfiguration;
import de.emesit.easi18n.I18nBundleService;
import de.emesit.easi18n.Language;


@Import({
    JacksonConfig.class, 
    I18nAutoConfiguration.class
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
