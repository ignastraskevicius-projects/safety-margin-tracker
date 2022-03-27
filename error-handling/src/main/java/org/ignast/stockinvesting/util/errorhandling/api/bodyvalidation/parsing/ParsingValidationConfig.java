package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParsingValidationConfig {

    @Bean
    JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor() {
        return new JacksonParsingErrorsExtractor();
    }
}
