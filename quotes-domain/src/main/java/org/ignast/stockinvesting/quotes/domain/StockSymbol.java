package org.ignast.stockinvesting.quotes.domain;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@EqualsAndHashCode
@Embeddable
public final class StockSymbol {
    @Column(name = "stock_symbol")
    private String symbol;

    protected StockSymbol() {
        //JPA requirement entities to have a default constructor
    }

    public StockSymbol(@NonNull String symbol) {
        if (symbol.isEmpty() || symbol.length() > 6) {
            throw new IllegalArgumentException("Stock Symbol must contain between 1-6 characters");
        }
        if (!symbol.matches("^[A-Z0-9]*$")) {
            throw new IllegalArgumentException("Stock Symbol must contain only uppercase alphanumeric characters");
        }
        this.symbol = symbol;
    }

    public String get() {
        return symbol;
    }
}
