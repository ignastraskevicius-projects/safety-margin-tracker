package org.ignast.stockinvesting.util.errorhandling.api.integrationtest.wiring.manual;

import org.ignast.stockinvesting.util.errorhandling.api.AnnotationBasedValidationErrorsExtractor;
import org.ignast.stockinvesting.util.errorhandling.api.ControllerAdviceForGenericErrors;
import org.ignast.stockinvesting.util.errorhandling.api.ErrorExtractorConfiguration;
import org.ignast.stockinvesting.util.errorhandling.api.GenericErrorController;
import org.ignast.stockinvesting.util.errorhandling.api.JacksonParsingErrorsExtractor;
import org.ignast.stockinvesting.util.errorhandling.api.interceptor.MediaTypeInterceptorConfig;
import org.ignast.stockinvesting.util.errorhandling.api.strictjackson.StrictIntegerDeserializer;
import org.ignast.stockinvesting.util.errorhandling.api.strictjackson.StrictStringDeserializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({MediaTypeInterceptorConfig.class, ErrorExtractorConfiguration.class})
final class GenericErrorHandlingConfiguration {

    @Bean
    public ErrorController errorController() {
        return new GenericErrorController();
    }

    @Bean
    public StrictStringDeserializer strictStringDeserializer() {
        return new StrictStringDeserializer();
    }

    @Bean
    public StrictIntegerDeserializer strictIntegerDeserializer() {
        return new StrictIntegerDeserializer();
    }

    @Bean
    public ControllerAdviceForGenericErrors genericControllerAdvice(
            JacksonParsingErrorsExtractor parsing,
            AnnotationBasedValidationErrorsExtractor validation) {
        return new ControllerAdviceForGenericErrors(validation, parsing);
    }
}