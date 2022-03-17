package org.ignast.stockinvesting.util.errorhandling.api;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.dtoParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.integerParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.listParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.stringParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.undefinedTypeParsingException;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.unexpectedTypeParsingFailed;
import static org.ignast.stockinvesting.util.errorhandling.api.ReferenceMock.toField;

final class JacksonParsingErrorsExtractorTest {

    private final JacksonParsingErrorsExtractor extractor = new JacksonParsingErrorsExtractor();

    @Test
    public void shouldFailToExtractErrorsForNullPath() {
        asList(integerParsingFailedAt(null), stringParsingFailedAt(null), dtoParsingFailedAt(null), listParsingFailedAt(null)).stream()
                .forEach(e -> assertThatExceptionOfType(JacksonParsingErrorExtractionException.class)
                        .isThrownBy(() -> extractor.extractError(e))
                        .withMessage("Jackson parsing failed without target type"));
    }

    @Test
    public void shouldExtractErrorsForRootPath() {
        asList(integerParsingFailedAt(asList()), stringParsingFailedAt(asList()), dtoParsingFailedAt(asList()), listParsingFailedAt(asList())).stream()
                .forEach(e -> assertThat(extractor.extractError(e).getJsonPath()).isEqualTo("$"));
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeString() {
        ValidationErrorDTO validationError = extractor.extractError(stringParsingFailedAt(asList()));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeString");
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeNumber() {
        ValidationErrorDTO validationError = extractor.extractError(integerParsingFailedAt(asList()));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeInteger");
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeObject() {
        ValidationErrorDTO validationError = extractor.extractError(dtoParsingFailedAt(asList()));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeObject");
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeArray() {
        ValidationErrorDTO validationError = extractor.extractError(listParsingFailedAt(asList()));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeArray");
    }

    @Test
    public void shouldFailToExtractErrorWithoutTargetType() {
        assertThatExceptionOfType(JacksonParsingErrorExtractionException.class)
                .isThrownBy(() -> extractor.extractError(undefinedTypeParsingException()))
                .withMessage("Jackson parsing failed with no target type defined");
    }

    @Test
    public void shouldFailToExtractErrorForUnexpectedType() {
        assertThatExceptionOfType(JacksonParsingErrorExtractionException.class)
                .isThrownBy(() -> extractor.extractError(unexpectedTypeParsingFailed()))
                .withMessage("Jackson parsing failed on unexpected target type");
    }

    @Test
    public void shouldPreserveJsonPathForFields() {
        List<Reference> path = asList(toField(new CityDTO(), "population"));

        asList(integerParsingFailedAt(path), stringParsingFailedAt(path), dtoParsingFailedAt(path), listParsingFailedAt(path)).stream()
                .map(e -> extractor.extractError(e))
                .forEach(e -> assertThat(e.getJsonPath()).isEqualTo("$.population"));
    }

    @Test
    public void shouldPreserveJsonPathForNestedFields() {
        List<Reference> path = asList(toField(new CityDTO(), "population"), toField(new PopulationDTO(), "growth"));

        asList(integerParsingFailedAt(path), stringParsingFailedAt(path), dtoParsingFailedAt(path), listParsingFailedAt(path)).stream()
                .map(e -> extractor.extractError(e))
                .forEach(e -> assertThat(e.getJsonPath()).isEqualTo("$.population.growth"));
    }

    @Test
    public void shouldPreserveJsonPathForArrays() {
        List<Reference> path = asList(ReferenceMock.toIndex(new ArrayList(), 5));

        asList(integerParsingFailedAt(path), stringParsingFailedAt(path), dtoParsingFailedAt(path), listParsingFailedAt(path)).stream()
                .map(e -> extractor.extractError(e)).forEach(e -> assertThat(e.getJsonPath()).isEqualTo("$[5]"));
    }

    class CityDTO {
    }

    class PopulationDTO {
    }
}