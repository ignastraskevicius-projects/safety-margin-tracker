package org.ignast.stockinvesting.api.controller.errorhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@JsonComponent
public class ErrorSerializer {
    private ObjectMapper mapper = new ObjectMapper();

    {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public ResponseEntity<String> serializeBodySchemaMismatchErrors(List<ValidationErrorDTO> errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(toJson(new BodyDoesNotMatchSchemaErrorDTO(errors)));
    }

    private String toJson(BodyDoesNotMatchSchemaErrorDTO error) {
        try {
            return mapper.writeValueAsString(error);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<String> serializeUnknownClientError() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"errorName\":\"unknownError\"}");
    }
}
