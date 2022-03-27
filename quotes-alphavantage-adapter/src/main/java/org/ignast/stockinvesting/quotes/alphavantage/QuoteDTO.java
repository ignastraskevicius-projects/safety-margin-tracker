package org.ignast.stockinvesting.quotes.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
final class QuoteDTO {

    @Getter
    private final Optional<BigDecimal> price;

    public QuoteDTO(@NonNull @JsonProperty(value = "05. price") final Optional<BigDecimal> price) {
        this.price = price;
    }
}
