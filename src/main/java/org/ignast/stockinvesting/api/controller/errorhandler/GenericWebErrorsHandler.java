package org.ignast.stockinvesting.api.controller.errorhandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class GenericWebErrorsHandler {
    private AnnotationBasedValidationErrorsExtractor validationErrorsExtractor;
    private JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor;

    public GenericWebErrorsHandler(AnnotationBasedValidationErrorsExtractor validationErrorsExtractor,
            JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor) {
        this.validationErrorsExtractor = validationErrorsExtractor;
        this.jacksonParsingErrorsExtractor = jacksonParsingErrorsExtractor;
    }

    @ExceptionHandler
    public StandardErrorDTO handleMethodNotAllowed(HttpRequestMethodNotSupportedException error) {
        return StandardErrorDTO.createForMethodNotAllowed();
    }

    @ExceptionHandler
    public ResponseEntity<String> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException error) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("{\"errorName\":\"mediaTypeNotAcceptable\"}");
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUnsupportedContentType(HttpMediaTypeNotSupportedException error) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body("{\"errorName\":\"unsupportedContentType\"}");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        try {
            return StandardErrorDTO.createForBodyDoesNotMatchSchema(
                    validationErrorsExtractor.extractAnnotationBasedErrorsFrom(exception));
        } catch (ValidationErrorsExtractionException e) {
            return StandardErrorDTO.createUnknownError();
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
                return StandardErrorDTO.createUnknownError();
            }
        } else {
            return StandardErrorDTO.createBodyNotParsable();
        }
    }
}
