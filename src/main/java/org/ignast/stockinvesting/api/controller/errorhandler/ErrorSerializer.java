package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ErrorSerializer {
    ResponseEntity<String> serializeBodySchemaMismatchErrors(List<ValidationError> errors);
}
