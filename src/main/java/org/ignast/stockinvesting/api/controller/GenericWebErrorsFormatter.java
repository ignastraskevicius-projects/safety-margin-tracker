package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.ignast.stockinvesting.strictjackson.StrictStringDeserializingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GenericWebErrorsFormatter {
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

    @ExceptionHandler
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        FieldError error = exception.getBindingResult().getFieldErrors().get(0);
        String message = error.getDefaultMessage();
        String field = error.getField();
        if (Arrays.asList(Size.class, Pattern.class).contains(error.unwrap(ConstraintViolationImpl.class)
                .getConstraintDescriptor().getAnnotation().annotationType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format(
                            "{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"$.%s\",\"message\":\"%s\"}", field,
                            message));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format(
                    "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.%s\"}]}",
                    error.getField()));
        }
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUnparsableJson(HttpMessageNotReadableException error) throws Throwable {
        if (error.getCause() instanceof MismatchedInputException) {
            String jsonPath = extractJsonPath((MismatchedInputException) error.getCause());
            if (error.getCause() instanceof StrictStringDeserializingException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format(
                        "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldMustBeString\",\"jsonPath\":\"%s\"}]}",
                        jsonPath));
            } else {
                if (((MismatchedInputException) error.getCause()).getTargetType() == ArrayList.class) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(String.format("{\"errorName\":\"fieldMustBeArray\",\"jsonPath\":\"%s\"}", jsonPath));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(String.format("{\"errorName\":\"fieldMustBeObject\",\"jsonPath\":\"%s\"}", jsonPath));
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"errorName\":\"bodyNotParsable\"}");
        }
    }

    private String extractJsonPath(MismatchedInputException parsingException) {
        return parsingException.getPath().stream().map(r -> {
            if (r.getFrom() instanceof List) {
                return String.format("[%s]", r.getIndex());
            } else {
                return String.format(".%s", r.getFieldName());
            }
        }).collect(Collectors.joining("", "$", ""));
    }
}
