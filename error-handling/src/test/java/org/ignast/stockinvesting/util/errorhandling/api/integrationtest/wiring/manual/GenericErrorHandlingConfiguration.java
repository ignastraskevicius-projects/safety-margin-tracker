package org.ignast.stockinvesting.util.errorhandling.api.integrationtest.wiring.manual;

import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.BodyValidationConfig;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.ControllerAdviceForParsingValidation;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.JacksonParsingErrorsExtractor;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.strictjackson.StrictIntegerDeserializer;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.strictjackson.StrictStringDeserializer;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.AnnotationBasedValidationErrorsExtractor;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.ControllerAdviceForPostParsedValidation;
import org.ignast.stockinvesting.util.errorhandling.api.genericvalidation.ControllerAdviceForGenericErrors;
import org.ignast.stockinvesting.util.errorhandling.api.genericvalidation.GenericErrorController;
import org.ignast.stockinvesting.util.errorhandling.api.interceptor.MediaTypeInterceptorConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@TestConfiguration
@Import({ MediaTypeInterceptorConfig.class, BodyValidationConfig.class })
class GenericErrorHandlingConfiguration {

    @Bean
    public ErrorController errorController(final MediaType appMediaType) {
        return new GenericErrorController(appMediaType);
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
    public ControllerAdviceForGenericErrors genericControllerAdvice() {
        return new ControllerAdviceForGenericErrors(MediaType.APPLICATION_CBOR);
    }

    @Bean
    public ControllerAdviceForParsingValidation parsingValidationControllerAdvice(
        final JacksonParsingErrorsExtractor parsing
    ) {
        return new ControllerAdviceForParsingValidation(parsing);
    }

    @Bean
    public ControllerAdviceForPostParsedValidation postParsingValidationControllerAdvice(
        final AnnotationBasedValidationErrorsExtractor validation
    ) {
        return new ControllerAdviceForPostParsedValidation(validation);
    }
}
