package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.ignast.stockinvesting.api.controller.errorhandler.ViolationType.FIELD_IS_MISSING;
import static org.ignast.stockinvesting.api.controller.errorhandler.ViolationType.VALUE_INVALID;

@JsonComponent
public class AnnotationBasedValidationErrorsFilter {
    public List<ValidationError> extractAnnotationBasedErrorsFrom(MethodArgumentNotValidException exception) {
        if (CollectionUtils.isEmpty(exception.getBindingResult().getFieldErrors())) {
            return new ArrayList<>();
        } else {
            return exception.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> extractAnnotationClassCausingViolation(fieldError).map(c -> toViolationType(c))
                            .filter(Optional::isPresent).map(Optional::get)
                            .map(t -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage(), t)))
                    .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        }
    }

    private Optional<ViolationType> toViolationType(Class<? extends Annotation> annotationClass) {
        if (annotationClass == NotNull.class) {
            return Optional.of(FIELD_IS_MISSING);
        } else if (asList(Size.class, Pattern.class).contains(annotationClass)) {
            return Optional.of(VALUE_INVALID);
        } else {
            return Optional.empty();
        }
    }

    private Optional<Class<? extends Annotation>> extractAnnotationClassCausingViolation(FieldError fieldError) {
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
