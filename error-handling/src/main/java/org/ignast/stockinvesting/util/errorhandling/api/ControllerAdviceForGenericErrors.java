package org.ignast.stockinvesting.util.errorhandling.api;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.ignast.stockinvesting.util.errorhandling.api.*;
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

import static java.util.Arrays.asList;

@ControllerAdvice
public class ControllerAdviceForGenericErrors {
    private AnnotationBasedValidationErrorsExtractor validationErrorsExtractor;
    private JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor;

    public ControllerAdviceForGenericErrors(AnnotationBasedValidationErrorsExtractor validationErrorsExtractor,
                                            JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor) {
        this.validationErrorsExtractor = validationErrorsExtractor;
        this.jacksonParsingErrorsExtractor = jacksonParsingErrorsExtractor;
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleMethodNotAllowed(HttpRequestMethodNotSupportedException error) {
        return StandardErrorDTO.createForMethodNotAllowed();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException error) {
        return StandardErrorDTO.createForMediaTypeNotAcceptable();
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleUnsupportedContentType(HttpMediaTypeNotSupportedException error) {
        return StandardErrorDTO.createForUnsupportedContentType();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        try {
            return StandardErrorDTO.createForBodyDoesNotMatchSchema(
                    validationErrorsExtractor.extractAnnotationBasedErrorsFrom(exception));
        } catch (ValidationErrorsExtractionException e) {
            return StandardErrorDTO.createNameless();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleUnparsableJson(HttpMessageNotReadableException error) {

        if (error.getCause() instanceof MismatchedInputException) {
            try {
                return StandardErrorDTO.createForBodyDoesNotMatchSchema(asList(
                        jacksonParsingErrorsExtractor.extractError((MismatchedInputException) error.getCause())));
            } catch (JacksonParsingErrorExtractionException e) {
                return StandardErrorDTO.createNameless();
            }
        } else {
            return StandardErrorDTO.createBodyNotParsable();
        }
    }

}
