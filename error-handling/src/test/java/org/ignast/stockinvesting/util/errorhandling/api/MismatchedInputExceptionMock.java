package org.ignast.stockinvesting.util.errorhandling.api;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.ignast.stockinvesting.util.errorhandling.api.strictjackson.StrictIntegerDeserializingException;
import org.ignast.stockinvesting.util.errorhandling.api.strictjackson.StrictStringDeserializingException;
import org.ignast.stockinvesting.util.mockito.MockitoUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
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
    public static MismatchedInputException stringParsingFailedAt(List<Reference> path) {
        return MockitoUtils.mock(StrictStringDeserializingException.class, e -> {
            when(e.getPath()).thenReturn(path);
        });
    }

    public static MismatchedInputException integerParsingFailedAt(List<Reference> path) {
        return MockitoUtils.mock(StrictIntegerDeserializingException.class, e -> {
            when(e.getPath()).thenReturn(path);
        });
    }

    public static MismatchedInputException listParsingFailedAt(List<Reference> path) {
        return MockitoUtils.mock(MismatchedInputException.class, e -> {
            doReturn(ArrayList.class).when(e).getTargetType();
            when(e.getPath()).thenReturn(path);
        });
    }

    public static MismatchedInputException undefinedTypeParsingException() {
        return mock(MismatchedInputException.class);
    }

    public static MismatchedInputException dtoParsingFailedAt(List<Reference> path) {
        return MockitoUtils.mock(MismatchedInputException.class, e -> {
            doReturn(TestDTO.class).when(e).getTargetType();
            when(e.getPath()).thenReturn(path);
        });
    }

    public static MismatchedInputException unexpectedTypeParsingFailed() {
        return MockitoUtils.mock(MismatchedInputException.class, e -> doReturn(HashSet.class).when(e).getTargetType());
    }

    class TestDTO {
    }
}

final class ReferenceMock {

    public static Reference toField(Object sourceObject, String field) {
        return MockitoUtils.mock(Reference.class, r -> {
            when(r.getFrom()).thenReturn(sourceObject);
            when(r.getFieldName()).thenReturn(field);
        });
    }

    public static Reference toIndex(Object sourceObject, int index) {
        return MockitoUtils.mock(Reference.class, r -> {
            when(r.getFrom()).thenReturn(sourceObject);
            when(r.getIndex()).thenReturn(index);
        });
    }
}

final class ReferenceMockTest {
    @Test
    public void shouldCreateFieldPath() {
        City source = new City();
        String fieldName = "population";
        Reference reference = toField(source, fieldName);

        assertThat(reference.getFrom()).isSameAs(source);
        assertThat(reference.getFieldName()).isEqualTo(fieldName);
    }

    @Test
    public void shouldCreateIndexPath() {
        List source = new ArrayList();
        int index = 3;
        Reference reference = ReferenceMock.toIndex(source, index);

        assertThat(reference.getFrom()).isSameAs(source);
        assertThat(reference.getIndex()).isEqualTo(index);
    }

    class City {
    }
}

final class MismatchedInputExceptionMockTest {
    @Test
    public void shouldCreateStringInputMismatchException() {
        MismatchedInputException exception = stringParsingFailedAt(null);
        assertThat(exception).isInstanceOf(StrictStringDeserializingException.class);
    }

    @Test
    public void shouldCreateIntegerInputMismatchException() {
        MismatchedInputException exception = integerParsingFailedAt(null);
        assertThat(exception).isInstanceOf(StrictIntegerDeserializingException.class);
    }

    @Test
    public void shouldCreateListInputMismatchException() {
        MismatchedInputException exception = listParsingFailedAt(null);
        assertThat(exception).isInstanceOf(MismatchedInputException.class)
                .isNotInstanceOf(StrictStringDeserializingException.class);
        assertThat(List.class.isAssignableFrom(exception.getTargetType())).isTrue();
    }

    @Test
    public void shouldCreateUndefinedTypeParsingException() {
        MismatchedInputException exception = undefinedTypeParsingException();
        assertThat(exception).isInstanceOf(MismatchedInputException.class)
                .isNotInstanceOf(StrictStringDeserializingException.class);
        assertThat(exception.getTargetType()).isNull();
    }

    @Test
    public void shouldCreateDtoInputMismatchException() {
        MismatchedInputException exception = dtoParsingFailedAt(null);
        assertThat(exception).isInstanceOf(MismatchedInputException.class)
                .isNotInstanceOf(StrictStringDeserializingException.class);
        assertThat(exception.getTargetType().getName()).endsWith("DTO");
    }

    @Test
    public void shouldCreateUnexpectedInputMismatchException() {
        MismatchedInputException exception = unexpectedTypeParsingFailed();
        assertThat(exception).isInstanceOf(MismatchedInputException.class)
                .isNotInstanceOf(StrictStringDeserializingException.class);
        assertThat(exception.getTargetType().getName()).endsWith("Set");
    }

    @Test
    public void shouldCreateMismatchedInputExceptionsWithNullPath() {
        asList(integerParsingFailedAt(null), stringParsingFailedAt(null), listParsingFailedAt(null), dtoParsingFailedAt(null)).stream()
                .map(MismatchedInputException::getPath).forEach(p -> assertThat(p).isNull());
    }


    @Test
    public void shouldCreateMismatchedInputExceptionsWithPath() {
        City sourceObject = new City();
        String field = "population";
        List<Reference> path = asList(toField(sourceObject, field));
        asList(integerParsingFailedAt(path), stringParsingFailedAt(path), listParsingFailedAt(path), dtoParsingFailedAt(path)).stream()
                .map(MismatchedInputException::getPath).forEach(p -> {
                    assertThat(p).isNotNull().hasSize(1);
                    assertThat(p.get(0).getFrom()).isSameAs(sourceObject);
                    assertThat(p.get(0).getFieldName()).isEqualTo(field);
                });
    }

    class City {
    }
}