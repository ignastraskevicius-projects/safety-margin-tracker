package org.ignast.stockinvesting.util.errorhandling.api;

import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.ignast.stockinvesting.util.errorhandling.api.ViolationType.FIELD_IS_MISSING;
import static org.ignast.stockinvesting.util.errorhandling.api.ViolationType.VALUE_INVALID;

public final class AnnotationBasedValidationErrorsExtractor {
    public List<ValidationErrorDTO> extractAnnotationBasedErrorsFrom(MethodArgumentNotValidException exception) {
        if (CollectionUtils.isEmpty(exception.getBindingResult().getFieldErrors())) {
            throw new ValidationErrorsExtractionException(
                    "javax.validation exception is expected to contain at least 1 field error");
        } else {
            return exception.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> new ValidationErrorDTO(
                            JsonPath.adaptFromJavaxValidationPath(fieldError.getField()),
                            fieldError.getDefaultMessage(),
                            toViolationType(extractAnnotationClassCausingViolation(fieldError))))
                    .collect(Collectors.toList());
        }
    }

    private ViolationType toViolationType(Class<? extends Annotation> annotationClass) {
        if (annotationClass == NotNull.class) {
            return FIELD_IS_MISSING;
        } else if (asList(Size.class, Pattern.class, DomainClassConstraint.class)
                .contains(annotationClass)) {
            return VALUE_INVALID;
        } else {
            throw new ValidationErrorsExtractionException(String.format(
                    "Extraction of javax.validation error due to violation caused by annotation '%s' is not supported",
                    annotationClass.getName()));
        }
    }

    private Class<? extends Annotation> extractAnnotationClassCausingViolation(FieldError fieldError) {
        try {
            Class<? extends Annotation> annotation = extractViolation(fieldError).getConstraintDescriptor()
                    .getAnnotation().annotationType();
            requireNonNull(annotation);
            return annotation;
        } catch (NullPointerException e) {
            throw new ValidationErrorsExtractionException(
                    "Extraction of javax.validation error was caused by violation not defined via annotation", e);
        }
    }

    private ConstraintViolation extractViolation(FieldError fieldError) {
        try {
            return fieldError.unwrap(ConstraintViolation.class);
        } catch (IllegalArgumentException e) {
            throw new ValidationErrorsExtractionException(
                    "Expected javax.validation ConstraintViolation but validation failed due to a different cause", e);
        }
    }
}

class ValidationErrorsExtractionException extends RuntimeException {
    public ValidationErrorsExtractionException(String message) {
        super(message);
    }

    public ValidationErrorsExtractionException(String message, RuntimeException cause) {
        super(message, cause);
    }
}