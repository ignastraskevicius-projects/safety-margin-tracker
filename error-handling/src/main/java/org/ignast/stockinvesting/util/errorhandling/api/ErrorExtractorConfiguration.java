package org.ignast.stockinvesting.util.errorhandling.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
public class ErrorExtractorConfiguration {

    @Bean
    public JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor() {
        return new JacksonParsingErrorsExtractor();
    }

    @Bean
    public AnnotationBasedValidationErrorsExtractor annotationBasedValidationErrorsExtractor() {
        return new AnnotationBasedValidationErrorsExtractor();
    }
}
