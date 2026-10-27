package de.emesit.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import de.emesit.demo.i18n.Language;
import de.emesit.demo.i18n.SimpleTranslationService;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class TranslationInitializerTest {

    @Mock
    private SimpleTranslationService translationService;

    @Mock
    private ResourceLoader resourceLoader;

    private TranslationInitializer translationInitializer;

    private static final String DEFAULT_INITIAL_TRANSLATIONS_RESOURCE = "classpath:initial-translations.json";

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = JsonMapper.builder().enable(ACCEPT_CASE_INSENSITIVE_ENUMS).build();

        // Construct the initializer manually with the mocks and the real ObjectMapper
        translationInitializer = new TranslationInitializer(translationService, resourceLoader, objectMapper);

        // Inject the private @Value field manually using Spring's ReflectionTestUtils
        ReflectionTestUtils.setField(translationInitializer, "initialTranslationsResource", DEFAULT_INITIAL_TRANSLATIONS_RESOURCE);
    }

    @Test
    @DisplayName("Should successfully load translations and save them into the translation service")
    void shouldLoadTranslationsSuccessfully() throws Exception {
        // Given
        String json = """
            [
              {
                "key": "K1",
                "language": "de",
                "translation": "Deutscher Wert für K1"
              },
              {
                "key": "K1",
                "language": "en",
                "translation": "English Value for K1"
              }
            ]
            """;
        
        Resource resource = mock(Resource.class);
        byte[] fakeJsonBytes = json.getBytes();
        InputStream inputStream = new ByteArrayInputStream(fakeJsonBytes);
        
//        TranslationInitializer.InitialTranslation translation1 = 
//                new TranslationInitializer.InitialTranslation("greeting", Language.DE, "Hallo");
//        TranslationInitializer.InitialTranslation translation2 = 
//                new TranslationInitializer.InitialTranslation("greeting", Language.EN, "Hello");
//        List<TranslationInitializer.InitialTranslation> mockList = List.of(translation1, translation2);

        when(resourceLoader.getResource(DEFAULT_INITIAL_TRANSLATIONS_RESOURCE)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        
        // When
        translationInitializer.run();

        // Then
        verify(translationService).setTranslation("K1", Language.DE, "Deutscher Wert für K1");
        verify(translationService).setTranslation("K1", Language.EN, "English Value for K1");
    }
    
    @Test
    @DisplayName("Should successfully load empty initial translations list")
    void shouldLoadEmptyTranslationsListSuccessfully() throws Exception {
        // Given
        String json = "[ ]";
        
        Resource resource = mock(Resource.class);
        byte[] fakeJsonBytes = json.getBytes();
        InputStream inputStream = new ByteArrayInputStream(fakeJsonBytes);
        
        when(resourceLoader.getResource(DEFAULT_INITIAL_TRANSLATIONS_RESOURCE)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        
        // When
        translationInitializer.run();

        // Then
        assertEquals(0, translationService.keySize());
        verify(translationService, never()).setTranslation(any(), any(), any());
    }
    
    @Test
    @DisplayName("Should successfully load empty initial translations file")
    void shouldLoadEmptyTranslationsFileSuccessfully() throws Exception {
        // Given
        String json = "";
        
        Resource resource = mock(Resource.class);
        byte[] fakeJsonBytes = json.getBytes();
        InputStream inputStream = new ByteArrayInputStream(fakeJsonBytes);
        
        when(resourceLoader.getResource(DEFAULT_INITIAL_TRANSLATIONS_RESOURCE)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        
        // When
        translationInitializer.run();

        // Then
        assertEquals(0, translationService.keySize());
        verify(translationService, never()).setTranslation(any(), any(), any());
    }

    @Test
    @DisplayName("Should do nothing if the resource path configuration is null")
    void shouldDoNothingIfResourcePathIsNull() throws Exception {
        // Given
        ReflectionTestUtils.setField(translationInitializer, "initialTranslationsResource", null);

        // When
        translationInitializer.run();

        // Then
        verify(resourceLoader, never()).getResource(any());
        verify(translationService, never()).setTranslation(any(), any(), any());
    }

    @Test
    @DisplayName("Should do nothing if the resource does not exist")
    void shouldDoNothingIfResourceDoesNotExist() throws Exception {
        // Given
        ReflectionTestUtils.setField(translationInitializer, "initialTranslationsResource", "classpath:some-unavailable-resource.json");

        // When
        translationInitializer.run();

        // Then
        verify(resourceLoader, times(1)).getResource(any());
        verify(translationService, never()).setTranslation(any(), any(), any());
    }

    @Test
    @DisplayName("Should catch and handle exception gracefully when resource loading fails")
    void shouldHandleExceptionGracefully() throws Exception {
        // Given
        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource(DEFAULT_INITIAL_TRANSLATIONS_RESOURCE)).thenReturn(resource);
        
        // Simulate an I/O error when opening the resource stream
        doThrow(new IOException("File not readable")).when(resource).getInputStream();

        // When
        translationInitializer.run();

        // Then
        // The run method contains a try-catch block, so no exception should be thrown to the outside
        verify(translationService, never()).setTranslation(any(), any(), any());
    }
}
