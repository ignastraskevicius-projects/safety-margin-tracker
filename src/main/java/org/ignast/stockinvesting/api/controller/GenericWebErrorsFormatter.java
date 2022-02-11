package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
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
    public ResponseEntity<String> handleNotNullValidations(MethodArgumentNotValidException error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\"errorName\":\"fieldMustBeObject\",\"jsonPath\":\"$.address\"}");
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUnparsableJson(HttpMessageNotReadableException error) throws Throwable {
        if (error.getCause() instanceof StrictStringDeserializingException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"errorName\":\"fieldMustBeString\",\"jsonPath\":\"$.name\"}");
        } else if (error.getCause() instanceof MismatchedInputException) {
            String message = error.getCause().getMessage();
            if (message.contains("Cannot construct instance of")
                    || message.contains("Cannot deserialize value of type `org.ignast.stockinvesting.api.controller")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"errorName\":\"fieldMustBeObject\",\"jsonPath\":\"$.address\"}");
            } else {
                String jsonPath = "{\"errorName\":\"fieldIsMissing\"" + ((MismatchedInputException) error.getCause())
                        .getPath().stream().findAny().map(JsonMappingException.Reference::getFieldName)
                        .map(f -> ",\"jsonPath\":\"$." + f + "\"").orElse("") + "}";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonPath);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"errorName\":\"bodyNotParsable\"}");
        }
    }
}
