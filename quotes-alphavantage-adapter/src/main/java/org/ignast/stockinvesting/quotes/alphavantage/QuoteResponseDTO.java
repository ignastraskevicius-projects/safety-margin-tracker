package org.ignast.stockinvesting.quotes.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
final class QuoteResponseDTO {

    @Getter
    private final Optional<QuoteDTO> quote;

    @Getter
    private final Optional<String> error;

    public QuoteResponseDTO(
        @NonNull @JsonProperty("Global Quote") final Optional<QuoteDTO> quote,
        @NonNull @JsonProperty("Error Message") final Optional<String> error
    ) {
        this.quote = quote;
        this.error = error;
    }
}
