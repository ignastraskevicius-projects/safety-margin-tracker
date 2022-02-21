package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GenericWebErrorsHandlerTest {

    @Test
    public void shouldSerializeJavaxValidationErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        GenericWebErrorsHandler handler = new GenericWebErrorsHandler(ValidationErrorsExtractorMock.returningErrors(),
                ErrorSerializerMock.serializingBodyShemaMismatchErrors());

        ResponseEntity<String> actualResponse = handler.handleMethodArgumentNotValidException(exception);

        assertThat(actualResponse).isNotNull();
    }
}