package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class ErrorSerializerTest {

    private ErrorSerializer serializer = new ErrorSerializer();

    @Test
    public void shouldSerialize0FieldValidationErrors() {
        ResponseEntity<String> responseEntity = serializer.serializeInvalidRequestBody(Collections.emptyList());

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isEqualTo("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[]}");
    }

    @Test
    public void shouldSerializeMissingFieldValidationError() {
        ValidationError validationError = new ValidationError("somePath", "anyMessage", ViolationType.FIELD_IS_MISSING);

        ResponseEntity<String> responseEntity = serializer.serializeInvalidRequestBody(asList(validationError));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.somePath\"}]}");
    }

    @Test
    public void shouldSerializeMultipleMissingFieldValidationError() {
        ValidationError missingFieldError1 = new ValidationError("path1", "anyMessage", ViolationType.FIELD_IS_MISSING);
        ValidationError messingFieldError2 = new ValidationError("path2", "anyMessage", ViolationType.FIELD_IS_MISSING);

        ResponseEntity<String> responseEntity = serializer
                .serializeInvalidRequestBody(asList(missingFieldError1, messingFieldError2));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isEqualTo("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":["
                        + "{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.path1\"},"
                        + "{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.path2\"}" + "]}");
    }
}