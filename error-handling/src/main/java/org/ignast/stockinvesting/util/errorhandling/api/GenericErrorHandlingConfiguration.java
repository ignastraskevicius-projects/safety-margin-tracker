package org.ignast.stockinvesting.util.errorhandling.api;

import org.ignast.stockinvesting.util.errorhandling.api.AnnotationBasedValidationErrorsExtractor;
import org.ignast.stockinvesting.util.errorhandling.api.JacksonParsingErrorsExtractor;
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
