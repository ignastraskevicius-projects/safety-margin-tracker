package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.ViolationType.FIELD_IS_MISSING;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.ViolationType.VALUE_INVALID;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.annotation.DomainClassConstraint;
import org.ignast.stockinvesting.util.errorhandling.api.dto.ValidationErrorDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public final class AnnotationBasedValidationErrorsExtractor {

    public List<ValidationErrorDTO> extractAnnotationBasedErrorsFrom(
        final MethodArgumentNotValidException exception
    ) {
        if (CollectionUtils.isEmpty(exception.getBindingResult().getFieldErrors())) {
            throw new ExtractionException(
                "javax.validation exception is expected to contain at least 1 field error"
            );
        } else {
            return exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError ->
                    new JsPathParsingValidationErrorDTO(
                        JsonPath.adaptFromJavaxValidationPath(fieldError.getField()),
                        fieldError.getDefaultMessage(),
                        toViolationType(extractAnnotationClassCausingViolation(fieldError))
                    )
                )
                .collect(Collectors.toList());
        }
    }

    private ViolationType toViolationType(final Class<? extends Annotation> annotationClass) {
        if (annotationClass == NotNull.class) {
            return FIELD_IS_MISSING;
        } else if (asList(Size.class, Pattern.class, DomainClassConstraint.class).contains(annotationClass)) {
            return VALUE_INVALID;
        } else {
            throw new ExtractionException(
                String.format(
                    "Extraction of javax.validation error due to violation caused by annotation '%s' is not supported",
                    annotationClass.getName()
                )
            );
        }
    }

    private Class<? extends Annotation> extractAnnotationClassCausingViolation(final FieldError fieldError) {
        try {
            final val annotation = extractViolation(fieldError)
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType();
            requireNonNull(annotation);
            return annotation;
        } catch (NullPointerException e) {
            throw new ExtractionException(
                "Extraction of javax.validation error was caused by violation not defined via annotation",
                e
            );
        }
    }

    @SuppressWarnings("rawtypes")
    private ConstraintViolation extractViolation(final FieldError fieldError) {
        try {
            return fieldError.unwrap(ConstraintViolation.class);
        } catch (IllegalArgumentException e) {
            throw new ExtractionException(
                "Expected javax.validation ConstraintViolation but validation failed due to a different cause",
                e
            );
        }
    }

    public static final class ExtractionException extends RuntimeException {

        public ExtractionException(final String message) {
            super(message);
        }

        public ExtractionException(final String message, final RuntimeException cause) {
            super(message, cause);
        }
    }
}
