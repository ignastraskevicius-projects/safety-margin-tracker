package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ErrorSerializerMock {
    public static JsonErrorSerializer serializingBodyShemaMismatchErrors() {
        JsonErrorSerializer serializer = mock(JsonErrorSerializer.class);
        when(serializer.serializeBodySchemaMismatchErrors(notNull()))
                .thenReturn(ResponseEntity.badRequest().body("any2"));
        return serializer;
    }
}

class ErrorSerializerMockTest {

    @Test
    public void shouldSerializeBodySchemaMismatchErrors() {
        JsonErrorSerializer serializer = ErrorSerializerMock.serializingBodyShemaMismatchErrors();

        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(asList());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    public void shouldRejectNullErrors() {
        JsonErrorSerializer serializer = ErrorSerializerMock.serializingBodyShemaMismatchErrors();

        ResponseEntity<String> responseEntity = serializer.serializeBodySchemaMismatchErrors(null);

        assertThat(responseEntity).isNull();
    }
}
