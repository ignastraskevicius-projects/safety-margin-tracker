package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationErrorsExtractor {
    public List<ValidationError> extractAnotationBasedErrorsFrom(MethodArgumentNotValidException exception) {
        if (exception.getBindingResult().getFieldErrors() == null) {
            return new ArrayList<>();
        }
        if (exception.getBindingResult().getFieldErrors().isEmpty()) {
            return new ArrayList<>();
        } else {
            return Arrays.asList(new ValidationError(exception.getBindingResult().getFieldErrors().get(0).getField()));
        }
    }
}
