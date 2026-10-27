package de.emesit.demo.i18n;

import java.util.Map;
import java.util.SortedSet;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.emesit.easi18n.I18nBundleService;
import de.emesit.easi18n.Language;

/**
 * REST Controller for i18nBundleService.
 * This controller can be used to list all method names or translation keys.
 * 
 * All methods have been implemented as GET requests for easier handling via browser.
 */
@RestController
@RequestMapping("/api/i18n")
public class I18nController {
    private final I18nBundleService    i18nBundleService;

    public I18nController(I18nBundleService i18nBundleService) {
        this.i18nBundleService  = i18nBundleService;
    }

    /**
     * Gets all supported languages.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/i18n/languages HTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/i18n/languages}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/i18n/languages"}</li>
     * </ul>
     */
    @GetMapping(path = "/languages", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> getLanguages()
    {
        return ResponseEntity.ok(String.join(", ", Language.CODES));
    }

    /**
     * Gets all translation keys.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/i18n/keys HTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/i18n/keys}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/i18n/keys"}</li>
     * </ul>
     */
    @GetMapping(path = "/keys", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> getKeys()
    {
        return ResponseEntity.ok(String.join(", ", i18nBundleService.getTranslationKeys()));
    }

    /**
     * Gets all i18n method names.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/i18n/keys HTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/i18n/keys}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/i18n/keys"}</li>
     * </ul>
     */
    @GetMapping(path = "/methodnames", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> getMethodNames()
    {
        return ResponseEntity.ok(String.join(", ", i18nBundleService.getI18nMethodNames()));
    }


    /**
     * list all i18n method names with their translations.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/i18n/list HTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/i18n/list}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/i18n/list"}</li>
     * </ul>
     */
    @GetMapping(path = "/list", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> listTranslations(@RequestParam(required = false) boolean all)
    {
        StringBuilder sb = new StringBuilder();
        
        SortedSet<String> sortedKeys = i18nBundleService.getI18nMethodNames();
        Map<String, String[]> rawTranslations = i18nBundleService.getRawTranslations();
        
        sortedKeys.stream().forEach(key -> {
            String[] keyTranslations = rawTranslations.get(key);
            if (keyTranslations != null) {
                for (Language nextLanguage : Language.VALUES) {
                    String translation = (nextLanguage.getIndex() < keyTranslations.length) ? keyTranslations[nextLanguage.getIndex()] : null;
                    if (translation != null || all) {
                        sb.append(key + " [" + nextLanguage + "] = [" + translation + "]\n");
                    }
                }
            }
        });
        return ResponseEntity.ok(sb.toString());
    }
}
