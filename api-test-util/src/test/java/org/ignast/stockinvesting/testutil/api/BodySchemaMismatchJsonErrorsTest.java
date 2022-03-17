package org.ignast.stockinvesting.testutil.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class BodySchemaMismatchJsonErrorsTest {
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

