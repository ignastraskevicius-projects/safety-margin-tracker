package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ListingDTO {
    @NotNull
    private String marketIdentifier;

    @NotNull
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
