package org.ignast.stockinvesting.quotes.alphavantage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.math.BigDecimal;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public final class DeserializationTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldDeserializeError() throws JsonProcessingException {
        final val response = mapper.readValue("{\"Error Message\":\"Some human-readable error message\"}",
                QuoteResponseDTO.class);

        assertThat(response.getError()).isEqualTo(of("Some human-readable error message"));
    }

    @Test
    public void shouldNotDeserializeNullFields() throws JsonProcessingException {
        final val response = mapper.readValue("{\"Error Message\":null,\"Global Quote\":null}",
                QuoteResponseDTO.class);

        assertThat(response.getError()).isEmpty();
        assertThat(response.getQuote()).isEmpty();
    }

    @Test
    public void shouldNotDeserializeIfResponseIsEmpty() throws JsonProcessingException {
        final val response = mapper.readValue("{}", QuoteResponseDTO.class);

        assertThat(response.getError()).isEmpty();
        assertThat(response.getQuote()).isEmpty();
    }

    @Test
    public void shouldDeserializeQuote() throws JsonProcessingException {
        final val response = mapper.readValue("{\"Global Quote\":{\"05. price\":\"128.5000\"}}", QuoteResponseDTO.class);

        assertThat(response.getQuote()).isPresent();
        assertThat(response.getQuote().get().getPrice()).isEqualTo(of(new BigDecimal("128.5000")));
    }

    @Test
    public void shouldNotDeserializePriceIfNotProvided() throws JsonProcessingException {
        final val response = mapper.readValue("{\"Global Quote\":{}}", QuoteResponseDTO.class);

        assertThat(response.getQuote()).isPresent();
        assertThat(response.getQuote().get().getPrice()).isEmpty();
    }
}
