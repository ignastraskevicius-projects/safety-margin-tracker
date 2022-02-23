package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class JacksonParsingErrorsExtractorTest {

    private JacksonParsingErrorsExtractor extractor = new JacksonParsingErrorsExtractor();

    @Test
    public void shouldExtractErrorRequiringValueToBeString() {
        ValidationErrorDTO validationError = extractor
                .extractError(MismatchedInputExceptionMock.stringParsingFailedAt(null));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeString");
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeObject() {
        ValidationErrorDTO validationError = extractor
                .extractError(MismatchedInputExceptionMock.dtoParsingFailedAt(null));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeObject");
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeArray() {
        ValidationErrorDTO validationError = extractor
                .extractError(MismatchedInputExceptionMock.listParsingFailedAt(null));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeArray");
    }

    @Test
    public void shouldFailToExtractErrorWithoutTargetType() {
        assertThatExceptionOfType(JacksonParsingErrorExtractionException.class)
                .isThrownBy(() -> extractor.extractError(MismatchedInputExceptionMock.undefinedTypeParsingException()))
                .withMessage("Jackson parsing failed with no target type defined");
    }

    @Test
    public void shouldFailToExtractErrorForUnexpectedType() {
        assertThatExceptionOfType(JacksonParsingErrorExtractionException.class)
                .isThrownBy(() -> extractor.extractError(MismatchedInputExceptionMock.unexpectedTypeParsingFailed()))
                .withMessage("Jackson parsing failed on unexpected target type");
    }
}