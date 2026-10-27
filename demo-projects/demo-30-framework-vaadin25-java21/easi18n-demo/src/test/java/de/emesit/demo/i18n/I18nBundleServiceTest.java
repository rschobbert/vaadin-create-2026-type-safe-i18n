package de.emesit.demo.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.emesit.easi18n.I18nAutoConfiguration;
import de.emesit.easi18n.I18nBundleService;
import de.emesit.easi18n.Language;


@SpringBootTest(classes = {
        SimpleTranslationService.class,
        I18nAutoConfiguration.class
    })
public class I18nBundleServiceTest implements WithAssertions {

    @Autowired
    private I18nBundleService i18nBundleService;

    @Test
    void testGetTranslationKeys() {
        Set<String> actualKeys = i18nBundleService.getTranslationKeys();

        assertThat(actualKeys)
                .isNotEmpty()
                .contains("TASK_LIST", "FIELD_REQUIRED");
    }
    
    @Test
    void testGetI18n() {
        I18n i18n = (I18n)i18nBundleService.getI18n(Language.DE);
        assertEquals("Aufgaben Liste", i18n.taskList());
        assertEquals("Überladen ohne Parameter", i18n.overloaded());
        
        i18n.switchLanguage(Language.ES);
        assertEquals("", i18n.overloaded());
    }
}