package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GenericWebErrorsHandlerTest {

    @Test
    public void shouldExtractJavaxValidationErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        GenericWebErrorsHandler handler = new GenericWebErrorsHandler(ValidationErrorsExtractorMock.returningErrors());

        StandardErrorDTO error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error).isInstanceOf(BodyDoesNotMatchSchemaErrorDTO.class);
        assertThat(((BodyDoesNotMatchSchemaErrorDTO) error).getValidationErrors()).isNotEmpty();
    }

    @Test
    public void validationExtractorFailingToExtractExpectedErrorsShouldResultInUnknownErrorSerialized() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        GenericWebErrorsHandler handler = new GenericWebErrorsHandler(ValidationErrorsExtractorMock.failingToExtract());

        StandardErrorDTO error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error.getErrorName()).isEqualTo("unknownError");
    }
}