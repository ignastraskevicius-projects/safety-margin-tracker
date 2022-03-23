package org.ignast.stockinvesting.util.errorhandling.api;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public final class ControllerAdviceForGenericErrors {

    private final AnnotationBasedValidationErrorsExtractor validationErrorsExtractor;

    private final JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor;

    public ControllerAdviceForGenericErrors(
        final AnnotationBasedValidationErrorsExtractor validationErrorsExtractor,
        final JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor
    ) {
        this.validationErrorsExtractor = validationErrorsExtractor;
        this.jacksonParsingErrorsExtractor = jacksonParsingErrorsExtractor;
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleMethodNotAllowed(final HttpRequestMethodNotSupportedException e) {
        return StandardErrorDTO.createForMethodNotAllowed();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleMediaTypeNotAcceptable(final HttpMediaTypeNotAcceptableException e) {
        return StandardErrorDTO.createForMediaTypeNotAcceptable();
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleUnsupportedContentType(final HttpMediaTypeNotSupportedException e) {
        return StandardErrorDTO.createForUnsupportedContentType();
    }

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
        } catch (ValidationErrorsExtractionException e) {
            return StandardErrorDTO.createNameless(HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleUnparsableJson(final HttpMessageNotReadableException error) {
        if (error.getCause() instanceof MismatchedInputException) {
            try {
                return StandardErrorDTO.createForBodyDoesNotMatchSchema(
                    List.of(
                        jacksonParsingErrorsExtractor.extractError(
                            (MismatchedInputException) error.getCause()
                        )
                    )
                );
            } catch (JacksonParsingErrorExtractionException e) {
                return StandardErrorDTO.createNameless(HttpStatus.BAD_REQUEST);
            }
        } else {
            return StandardErrorDTO.createBodyNotParsable();
        }
    }
}
