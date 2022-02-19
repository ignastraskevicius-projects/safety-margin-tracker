package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ValidationErrorsExtractor {
    public List<ValidationError> extractAnnotationBasedErrorsFrom(MethodArgumentNotValidException exception) {
        if (CollectionUtils.isEmpty(exception.getBindingResult().getFieldErrors())) {
            return new ArrayList<>();
        } else {
            return exception.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> extractAnnotationCausingViolation(fieldError)
                            .map(a -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage())))
                    .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        }
    }

    private Optional<Class<? extends Annotation>> extractAnnotationCausingViolation(FieldError fieldError) {
        try {
            ConstraintViolation violation = extractViolationOrNull(fieldError);
            return Optional.of(violation.getConstraintDescriptor().getAnnotation().annotationType());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    private ConstraintViolation extractViolationOrNull(FieldError fieldError) {
        try {
            return fieldError.unwrap(ConstraintViolation.class);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
