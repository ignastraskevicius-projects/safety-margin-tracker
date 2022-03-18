package org.ignast.stockinvesting.util.errorhandling.api;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.strictjackson.StrictIntegerDeserializingException;
import org.ignast.stockinvesting.util.errorhandling.api.strictjackson.StrictStringDeserializingException;
import org.ignast.stockinvesting.util.mockito.MockitoUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.dtoParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.integerParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.listParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.stringParsingFailedAt;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.undefinedTypeParsingException;
import static org.ignast.stockinvesting.util.errorhandling.api.MismatchedInputExceptionMock.unexpectedTypeParsingFailed;
import static org.ignast.stockinvesting.util.errorhandling.api.ReferenceMock.toField;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MismatchedInputExceptionMock {
    private MismatchedInputExceptionMock() {

    }

    public static MismatchedInputException stringParsingFailedAt(final List<Reference> path) {
        return MockitoUtils.mock(StrictStringDeserializingException.class, e -> when(e.getPath()).thenReturn(path));
    }

    public static MismatchedInputException integerParsingFailedAt(final List<Reference> path) {
        return MockitoUtils.mock(StrictIntegerDeserializingException.class, e -> when(e.getPath()).thenReturn(path));
    }

    public static MismatchedInputException listParsingFailedAt(final List<Reference> path) {
        return MockitoUtils.mock(MismatchedInputException.class, e -> {
            doReturn(ArrayList.class).when(e).getTargetType();
            when(e.getPath()).thenReturn(path);
        });
    }

    public static MismatchedInputException undefinedTypeParsingException() {
        return mock(MismatchedInputException.class);
    }

    public static MismatchedInputException dtoParsingFailedAt(final List<Reference> path) {
        return MockitoUtils.mock(MismatchedInputException.class, e -> {
            doReturn(TestDTO.class).when(e).getTargetType();
            when(e.getPath()).thenReturn(path);
        });
    }

    public static MismatchedInputException unexpectedTypeParsingFailed() {
        return MockitoUtils.mock(MismatchedInputException.class, e -> doReturn(HashSet.class).when(e).getTargetType());
    }

    private static final class TestDTO {
    }
}

final class ReferenceMock {
    private ReferenceMock() {

    }

    public static Reference toField(final Object sourceObject, final String field) {
        return MockitoUtils.mock(Reference.class, r -> {
            when(r.getFrom()).thenReturn(sourceObject);
            when(r.getFieldName()).thenReturn(field);
        });
    }

    public static Reference toIndex(final Object sourceObject, final int index) {
        return MockitoUtils.mock(Reference.class, r -> {
            when(r.getFrom()).thenReturn(sourceObject);
            when(r.getIndex()).thenReturn(index);
        });
    }
}

final class ReferenceMockTest {
    @Test
    public void shouldCreateFieldPath() {
        final val source = new City();
        final val fieldName = "population";
        final val reference = toField(source, fieldName);

        assertThat(reference.getFrom()).isSameAs(source);
        assertThat(reference.getFieldName()).isEqualTo(fieldName);
    }

    @Test
    public void shouldCreateIndexPath() {
        final val source = List.of();
        final val index = 3;
        final val reference = ReferenceMock.toIndex(source, index);

        assertThat(reference.getFrom()).isSameAs(source);
        assertThat(reference.getIndex()).isEqualTo(index);
    }

    private static final class City {
    }
}

final class MismatchedInputExceptionMockTest {
    @Test
    public void shouldCreateStringInputMismatchException() {
        final val exception = stringParsingFailedAt(null);
        assertThat(exception).isInstanceOf(StrictStringDeserializingException.class);
    }

    @Test
    public void shouldCreateIntegerInputMismatchException() {
        final val exception = integerParsingFailedAt(null);
        assertThat(exception).isInstanceOf(StrictIntegerDeserializingException.class);
    }

    @Test
    public void shouldCreateListInputMismatchException() {
        final val exception = listParsingFailedAt(null);
        assertThat(exception).isInstanceOf(MismatchedInputException.class)
                .isNotInstanceOf(StrictStringDeserializingException.class);
        assertThat(List.class.isAssignableFrom(exception.getTargetType())).isTrue();
    }

    @Test
    public void shouldCreateUndefinedTypeParsingException() {
        final val exception = undefinedTypeParsingException();
        assertThat(exception).isInstanceOf(MismatchedInputException.class)
                .isNotInstanceOf(StrictStringDeserializingException.class);
        assertThat(exception.getTargetType()).isNull();
    }

    @Test
    public void shouldCreateDtoInputMismatchException() {
        final val exception = dtoParsingFailedAt(null);
        assertThat(exception).isInstanceOf(MismatchedInputException.class)
                .isNotInstanceOf(StrictStringDeserializingException.class);
        assertThat(exception.getTargetType().getName()).endsWith("DTO");
    }

    @Test
    public void shouldCreateUnexpectedInputMismatchException() {
        final val exception = unexpectedTypeParsingFailed();
        assertThat(exception).isInstanceOf(MismatchedInputException.class)
                .isNotInstanceOf(StrictStringDeserializingException.class);
        assertThat(exception.getTargetType().getName()).endsWith("Set");
    }

    @Test
    public void shouldCreateMismatchedInputExceptionsWithNullPath() {
        Stream.of(integerParsingFailedAt(null), stringParsingFailedAt(null), listParsingFailedAt(null), dtoParsingFailedAt(null))
                .map(MismatchedInputException::getPath).forEach(p -> assertThat(p).isNull());
    }


    @Test
    public void shouldCreateMismatchedInputExceptionsWithPath() {
        final val sourceObject = new City();
        final val field = "population";
        final val path = List.of(toField(sourceObject, field));
        Stream.of(integerParsingFailedAt(path), stringParsingFailedAt(path), listParsingFailedAt(path), dtoParsingFailedAt(path))
                .map(MismatchedInputException::getPath).forEach(p -> {
                    assertThat(p).isNotNull().hasSize(1);
                    assertThat(p.get(0).getFrom()).isSameAs(sourceObject);
                    assertThat(p.get(0).getFieldName()).isEqualTo(field);
                });
    }

    private static final class City {
    }
}