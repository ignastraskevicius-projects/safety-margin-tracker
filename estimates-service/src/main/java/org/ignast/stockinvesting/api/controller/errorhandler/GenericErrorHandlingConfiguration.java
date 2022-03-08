package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericErrorHandlingConfiguration {

    @Bean
    public JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor() {
        return new JacksonParsingErrorsExtractor();
    }

    @Bean
    public AnnotationBasedValidationErrorsExtractor annotationBasedValidationErrorsExtractor() {
        return new AnnotationBasedValidationErrorsExtractor();
    }
}
