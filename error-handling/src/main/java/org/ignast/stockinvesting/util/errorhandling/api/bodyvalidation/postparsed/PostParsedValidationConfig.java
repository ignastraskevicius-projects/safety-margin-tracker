package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostParsedValidationConfig {

    @Bean
    AnnotationBasedValidationErrorsExtractor annotationBasedValidationErrorsExtractor() {
        return new AnnotationBasedValidationErrorsExtractor();
    }
}
