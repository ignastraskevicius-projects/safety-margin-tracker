package org.ignast.stockinvesting.api.controller.errorhandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.ignast.stockinvesting.strictjackson.StrictStringDeserializingException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@ControllerAdvice
public class GenericWebErrorsHandler {
    private AnnotationBasedValidationErrorsExtractor validationErrorsExtractor;

    public GenericWebErrorsHandler(AnnotationBasedValidationErrorsExtractor validationErrorsExtractor) {
        this.validationErrorsExtractor = validationErrorsExtractor;
    }

    @ExceptionHandler
    public ResponseEntity<String> handleMethodNotAllowed(HttpRequestMethodNotSupportedException error) {
        return new ResponseEntity("{\"errorName\":\"methodNotAllowed\"}", HttpStatus.METHOD_NOT_ALLOWED);
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
    public StandardErrorDTO handleUnparsableJson(HttpMessageNotReadableException error) throws Throwable {
        if (error.getCause() instanceof MismatchedInputException) {
            String jsonPath = extractJsonPath((MismatchedInputException) error.getCause());
            if (error.getCause() instanceof StrictStringDeserializingException) {
                return StandardErrorDTO
                        .createForBodyDoesNotMatchSchema(asList(new ValidationErrorDTO(JsonPath.fromJsonPath(jsonPath),
                                "", ViolationType.VALUE_MUST_BE_STRING)));
            } else {
                if (((MismatchedInputException) error.getCause()).getTargetType() == ArrayList.class) {
                    return StandardErrorDTO.createForBodyDoesNotMatchSchema(asList(new ValidationErrorDTO(
                            JsonPath.fromJsonPath(jsonPath), "", ViolationType.VALUE_MUST_BE_ARRAY)));
                } else {
                    return StandardErrorDTO.createForBodyDoesNotMatchSchema(asList(new ValidationErrorDTO(
                            JsonPath.fromJsonPath(jsonPath), "", ViolationType.VALUE_MUST_BE_OBJECT)));
                }
            }
        } else {
            return StandardErrorDTO.createBodyNotParsable();
        }
    }

    private String extractJsonPath(MismatchedInputException parsingException) {
        return "$" + parsingException.getPath().stream().map(r -> {
            if (r.getFrom() instanceof List) {
                return String.format("[%s]", r.getIndex());
            } else {
                return String.format(".%s", r.getFieldName());
            }
        }).collect(Collectors.joining());

    }
}
