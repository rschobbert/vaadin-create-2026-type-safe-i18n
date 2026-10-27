package de.emesit.demo;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import de.emesit.demo.i18n.Language;
import de.emesit.demo.i18n.SimpleTranslationService;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * The TranslationInitializer initializes the SimpleTranslationService with
 * values found in the resource that is configured by
 * {@code app.i18n.initialTranslationsResource} in the application.properties.
 * Currently the configured value of the resource is
 * <p>
 * {@code src/main/resources/initial-translations.json}
 * <p>
 * You can use the TranslationController to customize these values (or add new ones) while application is running.
 */
@Component
public class TranslationInitializer implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TranslationInitializer.class);

    @Value("${app.i18n.initialTranslationsResource:#{null}}")
    private String initialTranslationsResource;

    // -------------------------------------
    // ----- record InitialTranslation -----
    // -------------------------------------
    public static record InitialTranslation(String key, Language language, String translation) {}


    private final SimpleTranslationService translationService;
    private final ResourceLoader           resourceLoader;
    private final ObjectMapper             objectMapper;

    public TranslationInitializer(SimpleTranslationService translationService, 
                                  ResourceLoader resourceLoader, 
                                  ObjectMapper objectMapper) 
    {
        this.translationService = translationService;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        Resource resource = (initialTranslationsResource != null) ? resourceLoader.getResource(initialTranslationsResource) : null;
        if (resource != null) {
            try (InputStream inputStream = resource.getInputStream()) {
                List<InitialTranslation> initialTranslations = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<List<InitialTranslation>>() {}
                );
                for (InitialTranslation it : initialTranslations) {
                    translationService.setTranslation(it.key(), it.language(), it.translation());
                }
                LOG.info("{} initial translations loaded successfully from {}", 
                        initialTranslations.size(), 
                        initialTranslationsResource);
            } catch (Exception exc) {
                LOG.error("Failed to load initial translations from {}", initialTranslationsResource, exc);
            }
        } else if (initialTranslationsResource != null) {
            LOG.warn("Resource is null. That means resource [{}] was unavailable", initialTranslationsResource);
            
        }
    }
}
