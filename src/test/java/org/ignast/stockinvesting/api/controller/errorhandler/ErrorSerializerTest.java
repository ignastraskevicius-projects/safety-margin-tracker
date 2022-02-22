package org.ignast.stockinvesting.api.controller.errorhandler;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.fluentjsonassert.JsonAssert.assertThatJson;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class ErrorSerializerTest {

    private ErrorSerializer serializer = new ErrorSerializer();

    @Test
    public void shouldSerializeUnknownClientError() throws JSONException {
        ResponseEntity<String> response = serializer.serializeUnknownClientError();

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThatJson(response.getBody()).isEqualTo("{\"errorName\":\"unknownError\"}");
    }
}

class ErrorSerializerForBodyDoesNotMatchSchemaErrorTest {

    private ErrorSerializer serializer = new ErrorSerializer();

    @Test
    public void shouldSerializeZeroFieldValidationErrors() {
        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(Collections.emptyList());

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isEqualTo("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[]}");
    }

    @Test
    public void shouldSerializeMissingFieldValidationError() {
        ValidationErrorDTO validationError = new ValidationErrorDTO("somePath", "anyMessage",
                ViolationType.FIELD_IS_MISSING);

        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(asList(validationError));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.somePath\"}]}");
    }

    @Test
    public void shouldSerializeInvalidValueValidationError() {
        ValidationErrorDTO validationError = new ValidationErrorDTO("somePath", "someMessage",
                ViolationType.VALUE_INVALID);

        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(asList(validationError));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"$.somePath\",\"message\":\"someMessage\"}]}");
    }

    @Test
    public void shouldSerializeValidationErrorRequiringString() throws JSONException {
        ValidationErrorDTO validationError = new ValidationErrorDTO("somePath", "any",
                ViolationType.VALUE_MUST_BE_STRING);

        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(asList(validationError));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThatJson(responseEntity.getBody()).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeString\",\"jsonPath\":\"$.somePath\"}]}");
    }

    @Test
    public void shouldSerializeValidationErrorRequiringArray() throws JSONException {
        ValidationErrorDTO validationError = new ValidationErrorDTO("somePath", "any",
                ViolationType.VALUE_MUST_BE_ARRAY);

        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(asList(validationError));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThatJson(responseEntity.getBody()).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeArray\",\"jsonPath\":\"$.somePath\"}]}");
    }

    @Test
    public void shouldSerializeMultipleInvalidValueValidationErrors() {
        ValidationErrorDTO invalidValueError1 = new ValidationErrorDTO("somePath1", "someMessage1",
                ViolationType.VALUE_INVALID);
        ValidationErrorDTO invalidValueError2 = new ValidationErrorDTO("somePath2", "someMessage2",
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
        ValidationErrorDTO missingFieldError1 = new ValidationErrorDTO("path1", "anyMessage",
                ViolationType.FIELD_IS_MISSING);
        ValidationErrorDTO messingFieldError2 = new ValidationErrorDTO("path2", "anyMessage",
                ViolationType.FIELD_IS_MISSING);

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
        ValidationErrorDTO invalidValueError1 = new ValidationErrorDTO("somePath1", "someMessage1",
                ViolationType.VALUE_INVALID);
        ValidationErrorDTO messingFieldError2 = new ValidationErrorDTO("path2", "anyMessage",
                ViolationType.FIELD_IS_MISSING);

        ResponseEntity<String> responseEntity = serializer
                .serializeBodySchemaMismatchErrors(asList(invalidValueError1, messingFieldError2));

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isEqualTo("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":["
                        + "{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"$.somePath1\",\"message\":\"someMessage1\"},"
                        + "{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.path2\"}]}");
    }
}