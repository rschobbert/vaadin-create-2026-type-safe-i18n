package de.emesit.demo;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.MapperFeature;

/**
 * Jackson Configuration which configures ObjectMapper to allow lowercase enums.
 * Otherwise an exception occurs when reading initial-translations.json.
 */
@Configuration
public class JacksonConfig {
    
    /**
     * Returns a lambda to configure the jackson JsonMapper.Builder when
     * the ApplicationContext is built. The resulting ObjectMapper will
     * allow lowercase enums.
     * 
     * @return  a lambda to configure the jackson JsonMapper.Builder
     */
    @Bean
    JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    }
}