package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorSerializer {
    public ResponseEntity<String> serializeInvalidRequestBody(List<ValidationError> error) {
        String json = error.stream()
                .map(e -> String.format("{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.%s\"}", e.getPath()))
                .collect(Collectors.joining(",", "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[",
                        "]}"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(json);
    }
}
