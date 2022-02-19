package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class ValidationErrorsExtractorTest {

    private ValidationErrorsExtractor errorsExtractor = new ValidationErrorsExtractor();

    @Test
    public void exceptionShouldAlwaysContainBindingResult() throws NoSuchMethodException {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new MethodArgumentNotValidException(anyMethodParameter(), null))
                .withMessageContaining("BindingResult");
    }

    @Test
    public void shouldExtractNoErrorsIfExceptionContainsNullFieldErrors() throws NoSuchMethodException {
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(null));

        assertThat(errorsExtractor.extractAnotationBasedErrorsFrom(exception)).isEmpty();
    }

    @Test
    public void shouldExtractNoErrorsIfExceptionContainsNoFieldErrors() throws NoSuchMethodException {
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(new ArrayList<>()));

        assertThat(errorsExtractor.extractAnotationBasedErrorsFrom(exception)).isEmpty();
    }

    @Test
    public void shouldExtractSingleError() throws NoSuchMethodException {
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(Arrays.asList(anyField())));

        assertThat(errorsExtractor.extractAnotationBasedErrorsFrom(exception)).hasSize(1);
    }

    private FieldError anyField() {
        return new FieldError("company", "name", "message");
    }

    private BindingResult bindingResultWithFieldErrorsOf(List<FieldError> fieldErrors) {
        BindingResult bindingResult = new DataBinder("").getBindingResult();
        BindingResult spyBindingResult = spy(bindingResult);
        doReturn(fieldErrors).when(spyBindingResult).getFieldErrors();
        return spyBindingResult;
    }

    private MethodParameter anyMethodParameter() throws NoSuchMethodException {
        Method anyMethod = String.class.getMethod("charAt", int.class);
        return new MethodParameter(anyMethod, 0);
    }

}