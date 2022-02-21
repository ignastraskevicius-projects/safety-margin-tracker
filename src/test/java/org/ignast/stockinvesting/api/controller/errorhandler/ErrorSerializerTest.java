package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class ErrorSerializerTest {

    private JsonErrorSerializer serializer = new JsonErrorSerializer();

    @Test
    public void shouldSerialize0FieldValidationErrors() {
        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(Collections.emptyList());

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isEqualTo("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[]}");
    }

    @Test
    public void shouldSerializeMissingFieldValidationError() {
        ValidationError validationError = new ValidationError("somePath", "anyMessage", ViolationType.FIELD_IS_MISSING);

        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(asList(validationError));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.somePath\"}]}");
    }

    @Test
    public void shouldSerializeInvalidValueValidationError() {
        ValidationError validationError = new ValidationError("somePath", "someMessage", ViolationType.VALUE_INVALID);

        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(asList(validationError));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"$.somePath\",\"message\":\"someMessage\"}]}");
    }

    @Test
    public void shouldSerializeMultipleInvalidValueValidationErrors() {
        ValidationError invalidValueError1 = new ValidationError("somePath1", "someMessage1",
                ViolationType.VALUE_INVALID);
        ValidationError invalidValueError2 = new ValidationError("somePath2", "someMessage2",
                ViolationType.VALUE_INVALID);

        ResponseEntity<String> responseEntity = serializer
                .serializeBodySchemaMismatchErrors(asList(invalidValueError1, invalidValueError2));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isEqualTo("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":["
                        + "{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"$.somePath1\",\"message\":\"someMessage1\"},"
                        + "{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"$.somePath2\",\"message\":\"someMessage2\"}]}");
    }

    @Test
    public void shouldSerializeMultipleMissingFieldValidationError() {
        ValidationError missingFieldError1 = new ValidationError("path1", "anyMessage", ViolationType.FIELD_IS_MISSING);
        ValidationError messingFieldError2 = new ValidationError("path2", "anyMessage", ViolationType.FIELD_IS_MISSING);

        ResponseEntity<String> responseEntity = serializer
                .serializeBodySchemaMismatchErrors(asList(missingFieldError1, messingFieldError2));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isEqualTo("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":["
                        + "{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.path1\"},"
                        + "{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.path2\"}" + "]}");
    }

    @Test
    public void shouldSerializeMultipleValidationErrorsOfDifferentTypes() {
        ValidationError invalidValueError1 = new ValidationError("somePath1", "someMessage1",
                ViolationType.VALUE_INVALID);
        ValidationError messingFieldError2 = new ValidationError("path2", "anyMessage", ViolationType.FIELD_IS_MISSING);

        ResponseEntity<String> responseEntity = serializer
                .serializeBodySchemaMismatchErrors(asList(invalidValueError1, messingFieldError2));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isEqualTo("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":["
                        + "{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"$.somePath1\",\"message\":\"someMessage1\"},"
                        + "{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.path2\"}]}");
    }
}