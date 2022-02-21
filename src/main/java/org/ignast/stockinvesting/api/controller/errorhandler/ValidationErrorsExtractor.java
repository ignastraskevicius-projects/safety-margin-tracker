package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

public interface ValidationErrorsExtractor {
    List<ValidationError> extractAnnotationBasedErrorsFrom(MethodArgumentNotValidException exception);
}
