package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationErrorsExtractor {
    public List<ValidationError> extractAnnotationBasedErrorsFrom(MethodArgumentNotValidException exception) {
        if (exception.getBindingResult().getFieldErrors() == null) {
            return new ArrayList<>();
        }
        if (exception.getBindingResult().getFieldErrors().isEmpty()) {
            return new ArrayList<>();
        } else {
            FieldError fieldError = exception.getBindingResult().getFieldErrors().get(0);
            return Arrays.asList(new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        }
    }
}
