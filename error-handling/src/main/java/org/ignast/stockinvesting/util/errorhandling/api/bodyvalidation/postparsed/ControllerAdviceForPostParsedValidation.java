package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.ignast.stockinvesting.util.errorhandling.api.dto.StandardErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdviceForPostParsedValidation {

    @NonNull
    private final AnnotationBasedValidationErrorsExtractor validationErrorsExtractor;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException exception
    ) {
        try {
            return StandardErrorDTO.createForBodyDoesNotMatchSchema(
                validationErrorsExtractor.extractAnnotationBasedErrorsFrom(exception)
            );
        } catch (AnnotationBasedValidationErrorsExtractor.ExtractionException e) {
            return StandardErrorDTO.createNameless(HttpStatus.BAD_REQUEST);
        }
    }
}
