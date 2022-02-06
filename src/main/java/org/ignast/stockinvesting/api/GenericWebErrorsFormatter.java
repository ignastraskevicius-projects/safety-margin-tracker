package org.ignast.stockinvesting.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GenericWebErrorsFormatter {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleMothodNotAllowed(HttpRequestMethodNotSupportedException error) {
        return new ResponseEntity("{\"errorName\":\"methodNotAllowed\"}", HttpStatus.METHOD_NOT_ALLOWED);
    }
}
