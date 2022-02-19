package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    public void ensureThatUnderlyingFieldNameIsNeverNull() {
        String fieldName = null;
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new FieldError("company", fieldName, "message"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "some.path", "some.other.path" })
    public void shouldExtractSingleError(String path) throws NoSuchMethodException {
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(Arrays.asList(fieldWithUnderlyingPath(path))));

        List<ValidationError> validationErrors = errorsExtractor.extractAnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).getPath()).isEqualTo(path);
    }

    private FieldError fieldWithUnderlyingPath(String underlyingPath) {
        return new FieldError("company", underlyingPath, "message");
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