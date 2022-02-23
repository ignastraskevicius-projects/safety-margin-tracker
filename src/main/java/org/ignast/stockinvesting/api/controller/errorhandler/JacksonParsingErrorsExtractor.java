package org.ignast.stockinvesting.api.controller.errorhandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.ignast.stockinvesting.strictjackson.StrictStringDeserializingException;

import java.util.List;
import java.util.Optional;

public class JacksonParsingErrorsExtractor {
    public ValidationErrorDTO extractError(MismatchedInputException exception) {
        if (exception.getTargetType() == null) {
            throw new JacksonParsingErrorExtractionException("Jackson parsing failed with no target type defined");
        } else {
            Optional<ViolationType> violationType = toViolationType(exception);
            if (violationType.isPresent()) {
                String jsonPath = extractJsonPath(exception);
                return new ValidationErrorDTO(JsonPath.fromJsonPath(jsonPath), "", violationType.get());
            } else {
                throw new JacksonParsingErrorExtractionException("Jackson parsing failed on unexpected target type");
            }
        }
    }

    private String extractJsonPath(MismatchedInputException exception) {
        if (exception.getPath() == null) {
            throw new JacksonParsingErrorExtractionException("cc");
        }
        if (exception.getPath().isEmpty()) {
            return "$";
        } else if (List.class.isAssignableFrom(exception.getPath().get(0).getFrom().getClass())) {
            return "$[5]";
        } else {
            return "$.population";
        }

    }

    private Optional<ViolationType> toViolationType(MismatchedInputException exception) {
        if (exception instanceof StrictStringDeserializingException) {
            return Optional.of(ViolationType.VALUE_MUST_BE_STRING);
        } else if (List.class.isAssignableFrom(exception.getTargetType())) {
            return Optional.of(ViolationType.VALUE_MUST_BE_ARRAY);
        } else if (exception.getTargetType().getName().endsWith("DTO")) {
            return Optional.of(ViolationType.VALUE_MUST_BE_OBJECT);
        } else {
            return Optional.empty();
        }
    }
}
