package org.ignast.stockinvesting.quotes.util.test.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BodySchemaMismatchJsonErrors {

    public static String forMissingFieldAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forTwoMissingFieldsAt(String jsonPath1, String jsonPath2) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}, {\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}]}",
                jsonPath1, jsonPath2);
    }

    public static String forStringRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeString\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forIntegerRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeInteger\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forObjectRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeObject\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forArrayRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeArray\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forInvalidValueAt(String jsonPath, String message) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"%s\",\"message\":\"%s\"}]}",
                jsonPath, message);
    }

    public static String forInvalidValuesAt(String jsonPath1, String message1, String jsonPath2, String message2) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"%s\",\"message\":\"%s\"},{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"%s\",\"message\":\"%s\"}]}",
                jsonPath1, message1, jsonPath2, message2);
    }
}

class BodySchemaMismatchJsonErrorsTest {
    @Test
    public void shouldCreateErrorJsonForMissingField() {
        assertThat(BodySchemaMismatchJsonErrors.forMissingFieldAt("someJsonPath")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"someJsonPath\"}]}");
    }

    @Test
    public void shouldCreateErrorJsonForMultipleMissingFields() {
        assertThat(BodySchemaMismatchJsonErrors.forTwoMissingFieldsAt("someJsonPath", "someOtherPath")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"someJsonPath\"}, {\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"someOtherPath\"}]}");
    }

    @Test
    public void shouldCreateErrorJsonForStringRequiredField() {
        assertThat(BodySchemaMismatchJsonErrors.forStringRequiredAt("someJsonPath")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeString\",\"jsonPath\":\"someJsonPath\"}]}");
    }

    @Test
    public void shouldCreateErrorJsonForIntegerRequiredField() {
        assertThat(BodySchemaMismatchJsonErrors.forIntegerRequiredAt("someJsonPath")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeInteger\",\"jsonPath\":\"someJsonPath\"}]}");
    }

    @Test
    public void shouldCreateErrorJsonForObjectRequiredField() {
        assertThat(BodySchemaMismatchJsonErrors.forObjectRequiredAt("someJsonPath")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeObject\",\"jsonPath\":\"someJsonPath\"}]}");
    }

    @Test
    public void shouldCreateErrorJsonForArrayRequiredField() {
        assertThat(BodySchemaMismatchJsonErrors.forArrayRequiredAt("someJsonPath")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeArray\",\"jsonPath\":\"someJsonPath\"}]}");
    }

    @Test
    void shouldCreateErrorJsonForInvalidValue() {
        assertThat(BodySchemaMismatchJsonErrors.forInvalidValueAt("someJsonPath", "someMessage")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"someJsonPath\",\"message\":\"someMessage\"}]}");
    }

    @Test
    void shouldCreateErrorJsonForMultipleInvalidValues() {
        assertThat(BodySchemaMismatchJsonErrors.forInvalidValuesAt("someJsonPath", "someMessage", "someOtherJsonPath",
                "someOtherMessage")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"someJsonPath\",\"message\":\"someMessage\"},{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"someOtherJsonPath\",\"message\":\"someOtherMessage\"}]}");
    }
}

