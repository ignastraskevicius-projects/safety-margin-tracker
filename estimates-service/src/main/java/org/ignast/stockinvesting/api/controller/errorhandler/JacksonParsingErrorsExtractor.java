package org.ignast.stockinvesting.api.controller.errorhandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.ignast.stockinvesting.strictjackson.StrictStringDeserializingException;
import org.springframework.boot.jackson.JsonComponent;

import java.util.List;
import java.util.stream.Collectors;

@JsonComponent
public class JacksonParsingErrorsExtractor {
    public ValidationErrorDTO extractError(MismatchedInputException exception) {
        JsonPath jsonPath = JsonPath.fromJsonPath(extractJsonPath(exception));
        return new ValidationErrorDTO(jsonPath, "", toViolationType(exception));
    }

    private String extractJsonPath(MismatchedInputException exception) {
        if (exception.getPath() == null) {
            throw new JacksonParsingErrorExtractionException("Jackson parsing failed without target type");
        }
        return exception.getPath().stream().map(r -> {
            if (List.class.isAssignableFrom(r.getFrom().getClass())) {
                return String.format("[%s]", r.getIndex());
            } else {
                return String.format(".%s", r.getFieldName());
            }
        }).collect(Collectors.joining("", "$", ""));
    }

    private ViolationType toViolationType(MismatchedInputException exception) {
        if (exception instanceof StrictStringDeserializingException) {
            return ViolationType.VALUE_MUST_BE_STRING;
        } else if (exception.getTargetType() == null) {
            throw new JacksonParsingErrorExtractionException("Jackson parsing failed with no target type defined");
        } else if (List.class.isAssignableFrom(exception.getTargetType())) {
            return ViolationType.VALUE_MUST_BE_ARRAY;
        } else if (exception.getTargetType().getName().endsWith("DTO")) {
            return ViolationType.VALUE_MUST_BE_OBJECT;
        } else {
            throw new JacksonParsingErrorExtractionException("Jackson parsing failed on unexpected target type");
        }
    }
}

class JacksonParsingErrorExtractionException extends RuntimeException {
    public JacksonParsingErrorExtractionException(String message) {
        super(message);
    }
}
