package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@JsonComponent
public class ErrorSerializer {
    public ResponseEntity<String> serializeBodySchemaMismatchErrors(List<ValidationError> errors) {
        String json = errors.stream().map(this::toJson).collect(wrapWithBodyDoesNotMatchSchema());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(json);
    }

    private Collector<CharSequence, ?, String> wrapWithBodyDoesNotMatchSchema() {
        return Collectors.joining(",", "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[", "]}");
    }

    private String toJson(ValidationError error) {
        if (error.getType() == ViolationType.VALUE_INVALID) {
            return String.format("{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"$.%s\",\"message\":\"%s\"}",
                    error.getPath(), error.getMessage());
        } else if (error.getType() == ViolationType.VALUE_MUST_BE_STRING) {
            return String.format("{\"errorName\":\"valueMustBeString\",\"jsonPath\":\"$.%s\"}", error.getPath());
        } else if (error.getType() == ViolationType.VALUE_MUST_BE_ARRAY) {
            return String.format("{\"errorName\":\"valueMustBeArray\",\"jsonPath\":\"$.%s\"}", error.getPath());
        } else {
            return String.format("{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.%s\"}", error.getPath());
        }
    }

    public ResponseEntity<String> serializeUnknownClientError() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"errorName\":\"unknownError\"}");
    }
}
