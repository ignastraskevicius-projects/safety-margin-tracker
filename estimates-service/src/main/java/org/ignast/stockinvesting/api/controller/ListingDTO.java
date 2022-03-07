package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ignast.stockinvesting.api.controller.errorhandler.annotations.DomainClassConstraint;
import org.ignast.stockinvesting.estimates.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.estimates.domain.StockSymbol;

import javax.validation.constraints.NotNull;

public class ListingDTO {
    @NotNull
    @DomainClassConstraint(domainClass = MarketIdentifierCode.class)
    private String marketIdentifier;

    @NotNull
    @DomainClassConstraint(domainClass = StockSymbol.class)
    private String stockSymbol;

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
