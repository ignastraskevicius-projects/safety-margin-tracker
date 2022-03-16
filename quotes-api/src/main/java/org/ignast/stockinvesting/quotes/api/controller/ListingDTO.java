package org.ignast.stockinvesting.quotes.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.ignast.stockinvesting.quotes.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.StockSymbol;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode
public final class ListingDTO {
    @NotNull
    @DomainClassConstraint(domainClass = MarketIdentifierCode.class)
    private final String marketIdentifier;

    @NotNull
    @DomainClassConstraint(domainClass = StockSymbol.class)
    private final String stockSymbol;

    public ListingDTO(@JsonProperty(value = "marketIdentifier") String marketIdentifier,
                      @JsonProperty(value = "stockSymbol") String stockSymbol) {
        this.marketIdentifier = marketIdentifier;
        this.stockSymbol = stockSymbol;
    }

    public String getMarketIdentifier() {
        return marketIdentifier;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }
}
