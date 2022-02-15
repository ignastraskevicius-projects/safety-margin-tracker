package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
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

import javax.validation.constraints.Size;
import java.util.List;

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
        if (exception.getBindingResult().getFieldErrors().get(0).unwrap(ConstraintViolationImpl.class)
                .getConstraintDescriptor().getAnnotation().annotationType() == Size.class) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"$.name\",\"message\":\"Company name must be between 1-256 characters\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.%s\"}",
                            exception.getBindingResult().getFieldErrors().get(0).getField()));
        }
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUnparsableJson(HttpMessageNotReadableException error) throws Throwable {
        if (error.getCause() instanceof StrictStringDeserializingException) {
            if (((StrictStringDeserializingException) error.getCause()).getPath().get(0).getFieldName() == "address") {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"errorName\":\"fieldMustBeString\",\"jsonPath\":\"$.address.country\"}");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"errorName\":\"fieldMustBeString\",\"jsonPath\":\"$.name\"}");
            }
        } else if (error.getCause() instanceof MismatchedInputException) {
            String message = error.getCause().getMessage();
            if (message.contains("Cannot construct instance of")
                    || message.contains("Cannot deserialize value of type `org.ignast.stockinvesting.api.controller")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"errorName\":\"fieldMustBeObject\",\"jsonPath\":\"$.address\"}");
            } else {
                List<JsonMappingException.Reference> path = ((MismatchedInputException) error.getCause()).getPath();
                if (path.get(0).getFieldName() == "address") {
                    if (path.size() == 2) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.address.country\"}");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.address\"}");
                    }
                } else if (path.get(0).getFieldName() == "name") {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.name\"}");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.listings\"}");
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"errorName\":\"bodyNotParsable\"}");
        }
    }
}
