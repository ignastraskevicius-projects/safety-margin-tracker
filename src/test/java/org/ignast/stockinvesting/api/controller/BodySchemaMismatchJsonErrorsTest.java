package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BodySchemaMismatchJsonErrorsTest {
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
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldMustBeString\",\"jsonPath\":\"someJsonPath\"}]}");
    }

    @Test
    public void shouldCreateErrorJsonForObjectRequiredField() {
        assertThat(BodySchemaMismatchJsonErrors.forObjectRequiredAt("someJsonPath")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldMustBeObject\",\"jsonPath\":\"someJsonPath\"}]}");
    }

    @Test
    public void shouldCreateErrorJsonForArrayRequiredField() {
        assertThat(BodySchemaMismatchJsonErrors.forArrayRequiredAt("someJsonPath")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldMustBeArray\",\"jsonPath\":\"someJsonPath\"}]}");
    }

    @Test
    void shouldCreateErrorJsonForInvalidValue() {
        assertThat(BodySchemaMismatchJsonErrors.forInvalidValueAt("someJsonPath", "someMessage")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"someJsonPath\",\"message\":\"someMessage\"}]}");
    }

}
