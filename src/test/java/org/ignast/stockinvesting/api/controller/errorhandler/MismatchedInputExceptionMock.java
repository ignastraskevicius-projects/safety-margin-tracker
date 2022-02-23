package org.ignast.stockinvesting.api.controller.errorhandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.ignast.stockinvesting.mockito.MockitoUtils;
import org.ignast.stockinvesting.strictjackson.StrictStringDeserializingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.controller.errorhandler.MismatchedInputExceptionMock.*;
import static org.mockito.Mockito.*;

public class MismatchedInputExceptionMock {
    public static MismatchedInputException stringParsingFailed() {
        return mock(StrictStringDeserializingException.class);
    }

    public static MismatchedInputException listParsingFailed() {
        return MockitoUtils.mock(MismatchedInputException.class,
                e -> doReturn(ArrayList.class).when(e).getTargetType());
    }

    public static MismatchedInputException undefinedTypeParsingException() {
        return mock(MismatchedInputException.class);
    }

    public static MismatchedInputException dtoParsingFailed() {
        return MockitoUtils.mock(MismatchedInputException.class, e -> doReturn(TestDTO.class).when(e).getTargetType());
    }

    public static MismatchedInputException unexpectedTypeParsingFailed() {
        return MockitoUtils.mock(MismatchedInputException.class, e -> doReturn(HashSet.class).when(e).getTargetType());
    }

    class TestDTO {
    }
}

class MismatchedInputExceptionMockTest {
    @Test
    public void shouldCreateStringInputMismatchException() {
        assertThat(MismatchedInputExceptionMock.stringParsingFailed())
                .isInstanceOf(StrictStringDeserializingException.class);
    }

    @Test
    public void shouldCreateListInputMismatchException() {
        MismatchedInputException exception = listParsingFailed();
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
        MismatchedInputException exception = dtoParsingFailed();
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
}