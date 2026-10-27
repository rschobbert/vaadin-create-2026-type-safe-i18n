package de.emesit.demo.i18n;

import java.util.Map;
import java.util.SortedSet;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller to demonstrate the possibility of a custom 'backend' CustomTranslationService.
 * This controller can be used to get and set the translations in the SimpleTranslationService, which
 * saves custom translations in a ConcurrentHashMap.
 * 
 * All methods have been implemented as GET requests for easier handling via browser.
 */
@RestController
@RequestMapping("/api/translation")
public class TranslationController {
    private final SimpleTranslationService translationService;

    public TranslationController(SimpleTranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * Gets all supported languages.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/translation/languages HTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/translation/languages}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/translation/languages"}</li>
     * </ul>
     */
    @GetMapping(path = "/languages", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> getLanguages()
    {
        return ResponseEntity.ok(String.join(", ", Language.CODES));
    }

    /**
     * Gets all translation keys, which have been saved in the CustomTranslationService.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/translation/keys HTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/translation/keys}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/translation/keys"}</li>
     * </ul>
     */
    @GetMapping(path = "/keys", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> getKeys()
    {
        return ResponseEntity.ok(String.join(", ", translationService.getSortedKeys()));
    }

    /**
     * Gets all translations, which have been saved in the CustomTranslationService.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/translation/listHTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/translation/list}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/translation/list"}</li>
     * </ul>
     */
    @GetMapping(path = "/list", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> listTranslations()
    {
        StringBuilder sb = new StringBuilder();
        
        SortedSet<String> sortedKeys = translationService.getSortedKeys();
        Map<String, Map<I18nLanguage, String>> rawTranslations = translationService.getRawTranslations();
        
        sortedKeys.stream().forEach(key -> {
            Map<I18nLanguage, String> innerMap = rawTranslations.get(key);
            if (innerMap != null) {
                // inside each key, sort by language
                innerMap.keySet().stream().sorted().forEach(language -> {
                    String translation = innerMap.get(language);
                    sb.append(key + " [" + language + "] = [" + translation + "]\n");
                });
            }
        });
        return ResponseEntity.ok(sb.toString());
    }
    
    /**
     * Gets a translation and optionally formats it with arguments.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/translation/get/TASK_LIST?language=de HTTP/1.1}</li>
     * <li>{@code GET /api/translation/get/FIELD_REQUIRED?language=en&arg=myField HTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/translation/get/TASK_LIST?language=de}</li>
     * <li>{@code http://localhost:8080/api/translation/get/FIELD_REQUIRED?language=en&arg=myField}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/translation/get/TASK_LIST?language=de"}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/translation/get/FIELD_REQUIRED?language=en&arg=myField"}</li>
     * </ul>
     */
    @GetMapping( path = "/get/{key}", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> getTranslation(
            @PathVariable String key,
            @RequestParam String language,
            @RequestParam(required = false) String...arg)
    {
        String resolvedText = translationService.getTranslation(Language.of(language), key, arg);
        return (resolvedText == null) 
                ? ResponseEntity.notFound().build() 
                : ResponseEntity.ok(resolvedText);
    }

    /**
     * Sets a translation via GET request (for easier handling via browser).
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/translation/set/TASK_LIST?language=de&translation=SPEZIAL%20AufgabenListe HTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/translation/set/TASK_LIST?language=de&translation=SPEZIAL AufgabenListe}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/translation/set/TASK_LIST?language=de&translation=SPEZIAL%20AufgabenListe"}</li>
     * </ul>
     */
    @GetMapping( path = "/set/{key}", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> setTranslation(
            @PathVariable String key,
            @RequestParam String language,
            @RequestParam String translation)
    {
        boolean wasSet = translationService.setTranslation(key, Language.of(language), translation);
        return wasSet 
                ? ResponseEntity.ok("set ["+key+" / "+language+"] = ["+translation+"]") 
                : ResponseEntity.notFound().build();
    }

    /**
     * Deletes a translation via GET request (for easier handling via browser).
     * <p>
     * Examples:
     * <ul>
     * <li>{@code GET /api/translation/delete/TASK_LIST?language=de HTTP/1.1}</li>
     * <li>{@code http://localhost:8080/api/translation/delete/TASK_LIST?language=de}</li>
     * <li>{@code curl -X GET "http://localhost:8080/api/translation/delete/TASK_LIST?language=de"}</li>
     * </ul>
     */
    @GetMapping( path = "/delete/{key}", produces = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<String> deleteTranslation(
            @PathVariable String key,
            @RequestParam String language)
    {
        boolean removed = translationService.setTranslation(key, Language.of(language), null);
        return removed 
                ? ResponseEntity.ok("deleted ["+key+" / "+language+"]") 
                : ResponseEntity.notFound().build();
    }
}
