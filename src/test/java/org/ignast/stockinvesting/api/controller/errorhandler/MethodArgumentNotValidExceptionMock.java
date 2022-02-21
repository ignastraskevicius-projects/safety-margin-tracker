package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.api.controller.errorhandler.MethodArgumentNotValidExceptionMock.anyMethodParameter;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExpectationsForMethodArgumentNotValidExceptionTest {

    @Test
    public void exceptionShouldAlwaysContainBindingResult() throws NoSuchMethodException {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new MethodArgumentNotValidException(anyMethodParameter(), null))
                .withMessageContaining("BindingResult");
    }

    @Test
    public void shouldThrowOnUnwrappingIfExceptionIsNotDueToConstraintViolation() throws NoSuchMethodException {
        FieldError fieldError = new FieldError("company", "anyName", "anyMessage");
        Object source = new Object();
        fieldError.wrap(source);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldError.unwrap(ConstraintViolation.class))
                .withMessageContaining("No source object of the given type available: ")
                .withMessageContaining("ConstraintViolation");
    }

    @Test
    public void shouldThrowOnUnwrappingIfThereIsNoErrorSourceIndicated() throws NoSuchMethodException {
        FieldError fieldError = new FieldError("company", "anyName", "anyMessage");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldError.unwrap(ConstraintViolation.class))
                .withMessageContaining("No source object of the given type available: ")
                .withMessageContaining("ConstraintViolation");
    }

    @Test
    public void shouldThrowIfThereIsNullSourceIndicated() {
        FieldError fieldError = new FieldError("company", "anyName", "anyMessage");
        fieldError.wrap(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldError.unwrap(ConstraintViolation.class))
                .withMessageContaining("No source object of the given type available: ")
                .withMessageContaining("ConstraintViolation");
    }
}

public class MethodArgumentNotValidExceptionMock {
    public static MethodArgumentNotValidException withFieldErrors(List<FieldError> fieldErrors) {
        return new MethodArgumentNotValidException(anyMethodParameter(), bindingResultWithFieldErrorsOf(fieldErrors));
    }

    public static MethodArgumentNotValidException withSourceNotBeingConstraintViolation() {
        FieldError fieldError = mockFieldErrorWithNameAndMessage("any", "any");
        when(fieldError.unwrap(any())).thenThrow(IllegalArgumentException.class);
        return withFieldErrors(asList(fieldError));
    }

    public static MethodArgumentNotValidException withViolation(ConstraintViolation violation) {
        FieldError fieldError = mockFieldErrorWithNameAndMessage("any", "any");
        when(fieldError.unwrap(any())).thenReturn(violation);
        return withFieldErrors(asList(fieldError));
    }

    private static FieldError mockFieldErrorWithNameAndMessage(String field, String defaultMessage) {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn(field);
        when(fieldError.getDefaultMessage()).thenReturn(defaultMessage);
        return fieldError;
    }

    static MethodParameter anyMethodParameter() {
        try {
            Method anyMethod = String.class.getMethod("charAt", int.class);
            return new MethodParameter(anyMethod, 0);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static BindingResult bindingResultWithFieldErrorsOf(List<FieldError> fieldErrors) {
        BindingResult result = mock(BindingResult.class);
        when(result.getFieldErrors()).thenReturn(fieldErrors);
        return result;
    }
}
