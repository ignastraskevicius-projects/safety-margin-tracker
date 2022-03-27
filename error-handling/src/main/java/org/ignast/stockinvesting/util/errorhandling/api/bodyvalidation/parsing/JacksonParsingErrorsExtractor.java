package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.strictjackson.StrictIntegerDeserializingException;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.strictjackson.StrictStringDeserializingException;
import org.ignast.stockinvesting.util.errorhandling.api.dto.ValidationErrorDTO;

public final class JacksonParsingErrorsExtractor {

    public ValidationErrorDTO extractError(final MismatchedInputException exception) {
        final val jsonPath = JsonPath.fromJsonPath(extractJsonPath(exception));
        return new JsPathParsingValidationErrorDTO(jsonPath, toViolationType(exception));
    }

    private String extractJsonPath(final MismatchedInputException exception) {
        if (exception.getPath() == null) {
            throw new ExtractionException("Jackson parsing failed without target type");
        }
        return exception
            .getPath()
            .stream()
            .map(r -> {
                if (List.class.isAssignableFrom(r.getFrom().getClass())) {
                    return String.format("[%s]", r.getIndex());
                } else {
                    return String.format(".%s", r.getFieldName());
                }
            })
            .collect(Collectors.joining("", "$", ""));
    }

    @SuppressWarnings("checkstyle:returncount")
    private ViolationType toViolationType(final MismatchedInputException exception) {
        if (exception instanceof StrictStringDeserializingException) {
            return ViolationType.VALUE_MUST_BE_STRING;
        } else if (exception instanceof StrictIntegerDeserializingException) {
            return ViolationType.VALUE_MUST_BE_INTEGER;
        } else if (exception.getTargetType() == null) {
            throw new ExtractionException("Jackson parsing failed with no target type defined");
        } else if (List.class.isAssignableFrom(exception.getTargetType())) {
            return ViolationType.VALUE_MUST_BE_ARRAY;
        } else if (exception.getTargetType().getName().endsWith("DTO")) {
            return ViolationType.VALUE_MUST_BE_OBJECT;
        } else {
            throw new ExtractionException("Jackson parsing failed on unexpected target type");
        }
    }

    public static final class ExtractionException extends RuntimeException {

        public ExtractionException(final String message) {
            super(message);
        }
    }
}
