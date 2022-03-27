package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.MismatchedInputExceptionMock.dtoParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.MismatchedInputExceptionMock.integerParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.MismatchedInputExceptionMock.listParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.MismatchedInputExceptionMock.stringParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.MismatchedInputExceptionMock.undefinedTypeParsingException;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.MismatchedInputExceptionMock.unexpectedTypeParsingFailed;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.ReferenceMock.toField;

import java.util.List;
import java.util.stream.Stream;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.JacksonParsingErrorsExtractor.ExtractionException;
import org.junit.jupiter.api.Test;

final class JacksonParsingErrorsExtractorTest {

    private final JacksonParsingErrorsExtractor extractor = new JacksonParsingErrorsExtractor();

    @Test
    public void shouldFailToExtractErrorsForNullPath() {
        Stream
            .of(
                integerParsingFailedAt(null),
                stringParsingFailedAt(null),
                dtoParsingFailedAt(null),
                listParsingFailedAt(null)
            )
            .forEach(e ->
                assertThatExceptionOfType(ExtractionException.class)
                    .isThrownBy(() -> extractor.extractError(e))
                    .withMessage("Jackson parsing failed without target type")
            );
    }

    @Test
    public void shouldExtractErrorsForRootPath() {
        Stream
            .of(
                integerParsingFailedAt(List.of()),
                stringParsingFailedAt(List.of()),
                dtoParsingFailedAt(List.of()),
                listParsingFailedAt(List.of())
            )
            .forEach(e -> assertThat(extractor.extractError(e).getJsonPath()).isEqualTo("$"));
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeString() {
        final val validationError = extractor.extractError(stringParsingFailedAt(List.of()));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeString");
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeNumber() {
        final val validationError = extractor.extractError(integerParsingFailedAt(List.of()));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeInteger");
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeObject() {
        final val validationError = extractor.extractError(dtoParsingFailedAt(List.of()));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeObject");
    }

    @Test
    public void shouldExtractErrorRequiringValueToBeArray() {
        final val validationError = extractor.extractError(listParsingFailedAt(List.of()));

        assertThat(validationError.getErrorName()).isEqualTo("valueMustBeArray");
    }

    @Test
    public void shouldFailToExtractErrorWithoutTargetType() {
        assertThatExceptionOfType(ExtractionException.class)
            .isThrownBy(() -> extractor.extractError(undefinedTypeParsingException()))
            .withMessage("Jackson parsing failed with no target type defined");
    }

    @Test
    public void shouldFailToExtractErrorForUnexpectedType() {
        assertThatExceptionOfType(ExtractionException.class)
            .isThrownBy(() -> extractor.extractError(unexpectedTypeParsingFailed()))
            .withMessage("Jackson parsing failed on unexpected target type");
    }

    @Test
    public void shouldPreserveJsonPathForFields() {
        final val path = List.of(toField(new CityDTO(), "population"));

        Stream
            .of(
                integerParsingFailedAt(path),
                stringParsingFailedAt(path),
                dtoParsingFailedAt(path),
                listParsingFailedAt(path)
            )
            .map(extractor::extractError)
            .forEach(e -> assertThat(e.getJsonPath()).isEqualTo("$.population"));
    }

    @Test
    public void shouldPreserveJsonPathForNestedFields() {
        final val path = asList(toField(new CityDTO(), "population"), toField(new PopulationDTO(), "growth"));

        Stream
            .of(
                integerParsingFailedAt(path),
                stringParsingFailedAt(path),
                dtoParsingFailedAt(path),
                listParsingFailedAt(path)
            )
            .map(extractor::extractError)
            .forEach(e -> assertThat(e.getJsonPath()).isEqualTo("$.population.growth"));
    }

    @Test
    public void shouldPreserveJsonPathForArrays() {
        final val path = List.of(ReferenceMock.toIndex(List.of(), 5));

        Stream
            .of(
                integerParsingFailedAt(path),
                stringParsingFailedAt(path),
                dtoParsingFailedAt(path),
                listParsingFailedAt(path)
            )
            .map(extractor::extractError)
            .forEach(e -> assertThat(e.getJsonPath()).isEqualTo("$[5]"));
    }

    private static final class CityDTO {}

    private static final class PopulationDTO {}
}
