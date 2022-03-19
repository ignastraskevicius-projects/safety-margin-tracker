package org.ignast.stockinvesting.quotes.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
@Embeddable
public class StockSymbol {

    private static final int MAX_LENGTH_FOUND_IN_SHANGHAI_EXCHANGE = 6;

    @Column(name = "stock_symbol")
    private String symbol;

    protected StockSymbol() {
        //JPA requirement entities to have a default constructor
    }

    public StockSymbol(@NonNull final String symbol) {
        if (symbol.isEmpty() || symbol.length() > MAX_LENGTH_FOUND_IN_SHANGHAI_EXCHANGE) {
            throw new IllegalArgumentException("Stock Symbol must contain between 1-6 characters");
        }
        if (!symbol.matches("^[A-Z0-9]*$")) {
            throw new IllegalArgumentException(
                "Stock Symbol must contain only uppercase alphanumeric characters"
            );
        }
        this.symbol = symbol;
    }

    public String get() {
        return symbol;
    }
}
